package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.QUserRelationsWithEvent;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.event.QEvent;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.dto.user.UserWhoInvitedToEventAsParticipantDTO;
import com.covenant.tribe.exeption.event.EventAlreadyExistException;
import com.covenant.tribe.exeption.event.EventNotFoundException;
import com.covenant.tribe.exeption.event.MessageDidntSendException;
import com.covenant.tribe.exeption.storage.FilesNotHandleException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.FirebaseService;
import com.covenant.tribe.util.mapper.EventMapper;
import com.covenant.tribe.util.querydsl.EventFilter;
import com.covenant.tribe.util.querydsl.PartsOfDay;
import com.covenant.tribe.util.querydsl.QPredicates;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;
    EventTypeRepository eventTypeRepository;
    FirebaseService firebaseService;
    FileStorageRepository fileStorageRepository;
    UserRepository userRepository;
    TagRepository tagRepository;
    EventMapper eventMapper;

    @Transactional(readOnly = true)
    public Page<SearchEventDTO> getEventsByFilter(EventFilter filter, Long currentUserId, Pageable pageable) {

        QPredicates qPredicates = QPredicates.builder();

        if (filter.getDistanceInMeters() != null && filter.getLongitude() != null &&
                filter.getLatitude() != null) {

            NumberExpression<Double> distance = Expressions.numberTemplate(
                    Double.class,
                    "ST_Distance(" + QEvent.event.eventAddress.eventPosition +
                            ", ST_MakePoint(" + filter.getLongitude()+ ", " + filter.getLatitude() + "))");

            qPredicates.add(filter.getDistanceInMeters(), distance::lt);
        }

        if (filter.getEventTypeId() != null) {
            qPredicates.add(filter.getEventTypeId(), QEvent.event.eventType.id::in);
        }

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            LocalDateTime startDateTime = filter.getStartDate().atTime(LocalTime.MIN);
            LocalDateTime endDateTime = filter.getEndDate().atTime(LocalTime.MAX);

            qPredicates.add(startDateTime, QEvent.event.startTime::after);
            qPredicates.add(endDateTime, QEvent.event.endTime::before);
        }

        if (filter.getNumberOfParticipantsMin() != null && filter.getNumberOfParticipantsMax() != null) {
            QUserRelationsWithEvent qUserRelationsWithEvent = QUserRelationsWithEvent.userRelationsWithEvent;

            qPredicates.add(filter.getNumberOfParticipantsMin(), QEvent.event.eventRelationsWithUser.size().goe(
                    JPAExpressions.select(qUserRelationsWithEvent.count())
                            .from(qUserRelationsWithEvent)
                            .where(qUserRelationsWithEvent.eventRelations.id.eq(QEvent.event.id))
                            .where(qUserRelationsWithEvent.isParticipant.isTrue())
            ));

            qPredicates.add(filter.getNumberOfParticipantsMax(), QEvent.event.eventRelationsWithUser.size().loe(
                    JPAExpressions.select(qUserRelationsWithEvent.count())
                            .from(qUserRelationsWithEvent)
                            .where(qUserRelationsWithEvent.eventRelations.id.eq(QEvent.event.id))
                            .where(qUserRelationsWithEvent.isParticipant.isTrue())
            ));
        }

        if (filter.getPartsOfDay() != null) {
            PartsOfDay partsOfDayFromClient = PartsOfDay.valueOf(filter.getPartsOfDay());

            qPredicates.add(
                    filter.getPartsOfDay(),
                    QEvent.event.startTime.hour().goe(Integer.valueOf(partsOfDayFromClient.getHour()))
                            .and(QEvent.event.endTime.hour().loe(Integer.valueOf(PartsOfDay.getNextEnumValue(partsOfDayFromClient).getHour()))));
        }

        if (filter.getDurationEventInHoursMin() != null && filter.getDurationEventInHoursMax() != null) {
            Predicate eventDurationInHours =
                    QEvent.event.endTime.hour().subtract(QEvent.event.startTime.hour()).goe(filter.getDurationEventInHoursMin())
                    .and(
                            QEvent.event.endTime.hour().subtract(QEvent.event.startTime.hour()).loe(filter.getDurationEventInHoursMax())
                    );

            qPredicates.add(filter.getDurationEventInHoursMin(), eventDurationInHours);
        }

        if (filter.getIsPresenceOfAlcohol() != null) {
            qPredicates.add(filter.getIsPresenceOfAlcohol(), QEvent.event.isPresenceOfAlcohol::eq);
        }

        if (filter.getIsFree() != null) {
            qPredicates.add(filter.getIsFree(), QEvent.event.isFree::eq);
        }

        if (filter.getIsEighteenYearLimit() != null) {
            qPredicates.add(filter.getIsEighteenYearLimit(), QEvent.event.isEighteenYearLimit::eq);
        }

        Predicate predicate = qPredicates.build();
        return eventRepository.findAll(predicate, pageable)
                .map(event -> eventMapper.mapToSearchEventDTO(event, currentUserId));
    }

    @Transactional
    @Override
    public DetailedEventInSearchDTO handleNewEvent(RequestTemplateForCreatingEventDTO eventDto) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        Event event = eventMapper.mapToEvent(eventDto);
        EventType eventType = eventTypeRepository
                .findById(eventDto.getEventTypeId())
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] Event type with id %s, dont exist", eventDto.getEventTypeId()
                    );
                    log.error(message);
                    return new EventNotFoundException(message);
                });

        if (eventDto.getSendToAllUsersByInterests() && !eventDto.getIsPrivate()) {
            List<Long> userIds = userRepository
                    .findAllByInterestingEventType(eventType)
                    .stream()
                    .map(User::getId)
                    .toList();
            sendInvitationsToUsers(eventDto.getEventTypeId(), userIds, eventDto.getEighteenYearLimit());
        }

        if (!eventDto.getNewEventTagNames().isEmpty()) {

            Set<Tag> newTags = eventDto.getNewEventTagNames().stream()
                    .map(String::toLowerCase)
                    .filter(tagName -> tagRepository.findTagByTagName(tagName).isEmpty())
                    .map(tagName -> Tag.builder().tagName(tagName).build())
                    .collect(Collectors.toSet());
            tagRepository.saveAll(newTags);
            eventType.addTags(newTags);
            eventTypeRepository.save(eventType);
            event.addTagList(newTags.stream().toList());
        }

        try {
            fileStorageRepository.addEventImages(eventDto.getAvatarsForAdding());
            event = saveEvent(event, event.getOrganizer().getId());
            fileStorageRepository.deleteUnnecessaryAvatars(eventDto.getAvatarsForDeleting());
        } catch (IOException e) {
            String message = String.format("[EXCEPTION] IOException with message: %s", e.getMessage());
            log.error(message, e);
            throw new FilesNotHandleException(message);
        }

        sendInvitationsToUsers(
                event.getId(),
                eventDto.getInvitedUserIds().stream().toList(),
                eventDto.getEighteenYearLimit()
        );

        DetailedEventInSearchDTO detailedEventInSearchDTO =
                eventMapper.mapToDetailedEventInSearchDTO(event, event.getOrganizer().getId());

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return detailedEventInSearchDTO;
    }

    private void sendInvitationsToUsers(Long eventId, List<Long> userIds, boolean ageRestriction) {
        List<String> firebaseIds = getFirebaseIds(userIds, ageRestriction);
        if (firebaseIds.isEmpty()) return;
        String message = "Текст приглашения нужно придумать, id мероприятия лежит в поле data";
        String title = "Gazgolder ждет тебя";
        try {
            firebaseService.sendNotificationsByFirebaseIds(firebaseIds, title, message, eventId);
        } catch (FirebaseMessagingException e) {
            String errMessage = String.format("Messages dont send because firebase return: %s", e.getMessage());
            log.error(errMessage);
            throw new MessageDidntSendException(errMessage);
        }
    }

    private List<String> getFirebaseIds(List<Long> userIds, boolean ageRestriction) {
        List<String> firebaseIds = null;
        if (ageRestriction) {
            ZonedDateTime nowMinusEighteenYears = Instant.now().atZone(ZoneId.systemDefault()).minusYears(18);

            firebaseIds = userRepository
                    .findAllById(userIds)
                    .stream()
                    .filter(user -> {
                        if (user.getBirthday() != null) {
                            return user.getBirthday()
                                    .atStartOfDay(ZoneId.systemDefault())
                                    .isBefore(nowMinusEighteenYears);
                        } else {
                            return true;
                        }
                    })
                    .map(User::getFirebaseId)
                    .toList();
        } else {
            firebaseIds = userRepository
                    .findAllById(userIds)
                    .stream()
                    .map(User::getFirebaseId)
                    .toList();
        }
        return firebaseIds;
    }

    @Transactional(readOnly = true)
    public DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        Event event = getEventById(eventId);
        DetailedEventInSearchDTO detailedEventInSearchDTO = eventMapper.mapToDetailedEventInSearchDTO(event, userId);

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return detailedEventInSearchDTO;
    }

    public Event saveEvent(Event event, Long organizerId) {
        if (eventRepository.findByEventNameAndStartTimeAndOrganizerId(
                event.getEventName(), event.getStartTime(), organizerId).isEmpty()) {

            return eventRepository.save(event);
        } else {
            String message = String.format(
                    "[EXCEPTION] Event with name %s and start time %s already exist",
                    event.getEventName(), event.getStartTime());
            throw new EventAlreadyExistException(message);
        }
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] event with id {}, does not exist", eventId);
                    return new EventNotFoundException(String.format("Event with id %s  does not exist", eventId));
                });
    }

    @Transactional
    @Override
    public Set<User> inviteUsersAsParticipantsToEvent(
            UserWhoInvitedToEventAsParticipantDTO userWhoInvitedToEventAsParticipantDTO, String eventId) {

        //todo: refactor method
        return null;
    }

    @Transactional
    @Override
    public void addUserToEventAsParticipant(Long eventId, Long userId) {
        /*Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format(
                                "Event with id %s  does not exist",
                                eventId)
                ));
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(
                                "User with id %s  does not exist",
                                eventId)
                ));
        event.addUserAsAsParticipantsEvent(user);*/
    }
}
