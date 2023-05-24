package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.QUserRelationsWithEvent;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventStatus;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.event.QEvent;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.dto.user.UserWhoInvitedToEventAsParticipantDTO;
import com.covenant.tribe.exeption.event.*;
import com.covenant.tribe.exeption.storage.FilesNotHandleException;
import com.covenant.tribe.exeption.event.UserAlreadyInvitedException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.exeption.event.UserRelationsWithEventNotFoundException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.FirebaseService;
import com.covenant.tribe.service.UserRelationsWithEventService;
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
import java.util.ArrayList;
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
    UserRelationsWithEventRepository userRelationsWithEventRepository;
    UserRelationsWithEventService userRelationsWithEventService;
    EventMapper eventMapper;

    @Transactional(readOnly = true)
    public Page<SearchEventDTO> getEventsByFilter(EventFilter filter, Long currentUserId, Pageable pageable) {
        QPredicates qPredicates = QPredicates.builder();


        qPredicates.add(Boolean.TRUE, QEvent.event.showEventInSearch.isTrue());
        qPredicates.add(EventStatus.PUBLISHED, QEvent.event.eventStatus.eq(EventStatus.PUBLISHED));
        if (filter.getDistanceInMeters() != null && filter.getLongitude() != null &&
                filter.getLatitude() != null) {

            NumberExpression<Double> distance = Expressions.numberTemplate(
                    Double.class,
                    "ST_Distance(" + QEvent.event.eventAddress.eventPosition +
                            ", ST_MakePoint(" + filter.getLongitude() + ", " + filter.getLatitude() + "))");

            qPredicates.add(filter.getDistanceInMeters(), distance::lt);
        }
        if (filter.getEventTypeId() != null) {
            qPredicates.add(filter.getEventTypeId(), QEvent.event.eventType.id::in);
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            OffsetDateTime startDateTime = filter.getStartDate().atTime(LocalTime.MIN).atOffset(ZoneOffset.UTC);
            OffsetDateTime endDateTime = filter.getEndDate().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

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

        Page<Event> filteredEvents = eventRepository.findAll(predicate, pageable);

        if (currentUserId != null) {
            List<UserRelationsWithEvent> eventsCurrentUser = userRepository.findUserById(currentUserId)
                    .orElseThrow(() -> {
                        String message = String.format(
                                "[EXCEPTION] User with id %s, dont exist", currentUserId
                        );
                        log.error(message);
                        return new UserNotFoundException(message);
                    }).getUserRelationsWithEvents();

            return filteredEvents.map(event -> eventMapper.mapToSearchEventDTO(event, eventsCurrentUser));
        }
        return filteredEvents.map(eventMapper::mapToSearchEventDTO);
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
            sendInvitationsToUsers(eventDto.getEventTypeId(), userIds, eventDto.getIsEighteenYearLimit());
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
                eventDto.getIsEighteenYearLimit()
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
        checkEventStatus(event);
        DetailedEventInSearchDTO detailedEventInSearchDTO = eventMapper.mapToDetailedEventInSearchDTO(event, userId);

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return detailedEventInSearchDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventInUserProfileDTO> findEventsByOrganizerId(String organizerId) {
        Long organizerIdLong = Long.parseLong(organizerId);
        return eventRepository.findAllByOrganizerIdAndEventStatusIsNot(
                        organizerIdLong, EventStatus.DELETED
                )
                .stream()
                .map(event -> eventMapper.mapToEventInUserProfileDTO(event, organizerIdLong))
                .toList();
    }

    private void checkEventStatus(Event event) {
        if (event.getEventStatus() == EventStatus.VERIFICATION_PENDING) {
            String message = String.format("Event with id %s is not verified yet", event.getId());
            log.error(message);
            throw new EventNotVerifiedException(message);
        }
        if (event.getEventStatus() == EventStatus.DELETED) {
            String message = String.format("Event with id %s is deleted", event.getId());
            log.error(message);
            throw new EventNotFoundException(message);
        }
        if (event.getEventStatus() == EventStatus.SEND_TO_REWORK) {
            String message = String.format("Event with id %s is send to rework", event.getId());
            log.error(message);
            throw new EventNotVerifiedException(message);
        }
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

    @Transactional(readOnly = true)
    @Override
    public List<EventVerificationDTO> getEventWithVerificationPendingStatus() {
        return eventRepository
                .findAllByEventStatus(EventStatus.VERIFICATION_PENDING)
                .stream().map(eventMapper::mapToEventVerificationDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateEventStatusToPublished(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getEventStatus() != EventStatus.VERIFICATION_PENDING) {
            String message = String.format("[EXCEPTION] Event with id %s is already verified", eventId);
            log.error(message);
            throw new EventAlreadyVerifiedException(message);
        }
        event.setEventStatus(EventStatus.PUBLISHED);
        eventRepository.save(event);
    }

    @Transactional
    @Override
    public void updateEventStatusToSendToRework(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getEventStatus() != EventStatus.VERIFICATION_PENDING) {
            String message = String.format("[EXCEPTION] Event with id %s is already verified or send to rework", eventId);
            log.error(message);
            throw new EventAlreadyVerifiedException(message);
        }
        event.setEventStatus(EventStatus.SEND_TO_REWORK);
        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventInUserProfileDTO> findEventsByUserIdWhichUserIsInvited(String userId) {
        User user = getUser(userId);
        List<UserRelationsWithEvent> userRelationsWithEvents = getUserRelationsWithEvents(user);

        return userRelationsWithEvents.stream()
                .filter(userRelationsWithEvent -> {
                    return userRelationsWithEvent.getEventRelations().getEventStatus() == EventStatus.PUBLISHED
                            && userRelationsWithEvent.isInvited();
                })
                .map(userRelationsWithEvent -> eventMapper.mapToEventInUserProfileDTO(
                        userRelationsWithEvent.getEventRelations(), userRelationsWithEvent.getUserRelations().getId())
                )
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventInUserProfileDTO> findEventsByUserIdWhichUserIsParticipant(String userId) {
        User user = getUser(userId);
        List<UserRelationsWithEvent> userRelationsWithEvents = getUserRelationsWithEvents(user);

        return userRelationsWithEvents.stream()
                .filter(userRelationsWithEvent -> {
                    return userRelationsWithEvent.getEventRelations().getEventStatus() == EventStatus.PUBLISHED
                            && userRelationsWithEvent.isParticipant();
                })
                .map(userRelationsWithEvent -> eventMapper.mapToEventInUserProfileDTO(
                        userRelationsWithEvent.getEventRelations(), userRelationsWithEvent.getUserRelations().getId())
                )
                .toList();
    }

    @Transactional
    @Override
    public void confirmInvitationToEvent(Long eventId, String userId) {
        UserRelationsWithEvent userRelationsWithEvent = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsIdAndIsInvitedTrue(Long.parseLong(userId), eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] User relations with id %s and event relations with id %s does not exist",
                            userId);
                    log.error(message);
                    return new UserRelationsWithEventNotFoundException(message);
                });
        userRelationsWithEvent.setParticipant(true);
        userRelationsWithEventRepository.save(userRelationsWithEvent);
    }

    @Transactional
    @Override
    public void declineInvitationToEvent(Long eventId, String userId) {
        UserRelationsWithEvent userRelationsWithEvent = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsIdAndIsInvitedTrue(Long.parseLong(userId), eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] User relations with id %s and event relations with id %s does not exist",
                            userId);
                    log.error(message);
                    return new UserRelationsWithEventNotFoundException(message);
                });
        userRelationsWithEvent.setInvited(false);
        userRelationsWithEventRepository.save(userRelationsWithEvent);
    }

    @Transactional
    @Override
    public void declineToParticipantInEvent(Long eventId, String userId) {
        UserRelationsWithEvent userRelationsWithEvent = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsIdAndIsParticipantTrue(eventId, Long.parseLong(userId))
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] User relations with id %s and event relations with id %s does not exist",
                            userId);
                    log.error(message);
                    return new UserRelationsWithEventNotFoundException(message);
                });
        userRelationsWithEvent.setParticipant(false);
        userRelationsWithEventRepository.save(userRelationsWithEvent);
    }

    @Transactional
    @Override
    public void deleteEvent(Long organizerId, Long eventId) {
        Event event = eventRepository
                .findByOrganizerIdAndId(organizerId, eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] Event with id %s and organizer with id %s, does not exist",
                            eventId, organizerId);
                    log.error(message);
                    return new EventNotFoundException(message);
                });
        event.setEventStatus(EventStatus.DELETED);
        eventRepository.save(event);
    }

    @Override
    public void sendToOrganizerRequestToParticipationInPrivateEvent(Long eventId, String userId) {
        boolean isUserAlreadyInvited = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsIdAndIsInvitedTrue(Long.parseLong(userId), eventId)
                .isPresent();
        if (isUserAlreadyInvited) {
            String message = String.format("[EXCEPTION] User with id %s is already invited to this event", userId);
            log.error(message);
            throw new UserAlreadyInvitedException(message);
        }
        boolean isUserAlreadyParticipant = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsIdAndIsParticipantTrue(eventId, Long.parseLong(userId))
                .isPresent();
        if (isUserAlreadyParticipant) {
            String message = String.format("[EXCEPTION] User with id %s is already participant in this event", userId);
            log.error(message);
            throw new UserAlreadyParticipantException(message);
        }
        boolean isUserAlreadySendRequest = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsIdAndIsWantToGoTrue(Long.parseLong(userId), eventId)
                .isPresent();
        if (isUserAlreadySendRequest) {
            String message = String.format("[EXCEPTION] User with id %s is already send request to this event", userId);
            log.error(message);
            throw new UserAlreadySendRequestException(message);
        }
        UserRelationsWithEvent userRelationsWithEvent = UserRelationsWithEvent.builder()
                .isInvited(false)
                .isParticipant(false)
                .isWantToGo(true)
                .isFavorite(false)
                .isViewed(false)
                .build();
        User user = getUser(userId);
        Event event = getEventById(eventId);
        if (!event.isPrivate()) {
            String message = String.format("[EXCEPTION] Event with id %s is not closed", eventId);
            log.error(message);
            throw new NotPrivateEventException(message);
        }
        userRelationsWithEvent.setEventRelations(event);
        userRelationsWithEvent.setUserRelations(user);
        userRelationsWithEventRepository.save(userRelationsWithEvent);
    }

    @Override
    public void sendRequestToParticipationInPublicEvent(Long eventId, String userId) {
        Event event = getEventById(eventId);
        if (event.isPrivate()) {
            String message = String.format("[EXCEPTION] Event with id %s is not public", eventId);
            log.error(message);
            throw new NotPublicEventException(message);
        }
        UserRelationsWithEvent userRelationsWithEvent = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsId(Long.parseLong(userId), eventId)
                .orElseGet(() -> {
                    return null;
                });
        if (userRelationsWithEvent == null) {
            userRelationsWithEvent = UserRelationsWithEvent.builder()
                    .isInvited(false)
                    .isParticipant(true)
                    .isWantToGo(false)
                    .isFavorite(false)
                    .isViewed(false)
                    .build();
            User user = getUser(userId);
            userRelationsWithEvent.setEventRelations(event);
            userRelationsWithEvent.setUserRelations(user);
        } else {
            userRelationsWithEvent.setInvited(false);
            userRelationsWithEvent.setWantToGo(false);
            userRelationsWithEvent.setParticipant(true);
        }
        userRelationsWithEventRepository.save(userRelationsWithEvent);
    }


    private List<UserRelationsWithEvent> getUserRelationsWithEvents(User user) {
        List<UserRelationsWithEvent> userRelationsWithEvents =
                userRelationsWithEventRepository.findAllByUserRelations(user);
        if (userRelationsWithEvents.isEmpty()) {
            return new ArrayList<>();
        }
        return userRelationsWithEvents;
    }

    private User getUser(String userId) {
        User user = userRepository
                .findById(Long.parseLong(userId))
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] User with id %s, does not exist",
                            userId);
                    log.error(message);
                    return new UserNotFoundException(message);
                });
        return user;
    }

    @Transactional
    @Override
    public void addUsersToPrivateEventAsParticipants(Long eventId, Long userId) {
        Event event = getEventById(eventId);
        if (!event.isPrivate()) {
            String message = String.format("[EXCEPTION] Event with id %s is not private", eventId);
            log.error(message);
            throw new NotPrivateEventException(message);
        }
        List<UserRelationsWithEvent> userRelationsWithEvents = userRelationsWithEventRepository
                .findByEventRelationsIdAndIsWantToGo(eventId, true);
        userRelationsWithEvents.forEach(relation -> {
            relation.setParticipant(true);
            relation.setWantToGo(false);
        });
        userRelationsWithEventRepository.saveAll(userRelationsWithEvents);
    }

    @Transactional
    @Override
    public void addUserToPrivateEventAsParticipant(Long eventId, Long organizerId, Long userId) {
        Event event = getEventById(eventId);
        if (!event.isPrivate()) {
            String message = String.format("[EXCEPTION] Event with id %s is not private", eventId);
            log.error(message);
            throw new NotPrivateEventException(message);
        }
        UserRelationsWithEvent userRelationsWithEvent = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsId(userId, eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] User with id %s and event relations with id %s does not exist",
                            userId, eventId);
                    log.error(message);
                    return new UserRelationsWithEventNotFoundException(message);
                });
        if (userRelationsWithEvent.isInvited()) {
            userRelationsWithEvent.setInvited(false);
        }
        userRelationsWithEvent.setWantToGo(false);
        userRelationsWithEvent.setParticipant(true);
        userRelationsWithEventRepository.save(userRelationsWithEvent);
    }

    @Override
    public boolean isFavoriteEventForUser(Long userId, Long eventId) {
        return findUserById(userId).getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEventRelations().getId().equals(eventId))
                .findFirst()
                .map(UserRelationsWithEvent::isFavorite)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Event> getAllFavoritesByUserId(Long userId) {
        return findUserById(userId).getUserRelationsWithEvents().stream()
                .filter(UserRelationsWithEvent::isFavorite)
                .map(UserRelationsWithEvent::getEventRelations)
                .toList();
    }

    @Transactional
    @Override
    public void removeEventFromFavorite(Long userId, Long eventId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        User user = findUserById(userId).getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEventRelations().getId().equals(eventId))
                .findFirst()
                .map(userRelationsWithEvent -> {
                    userRelationsWithEvent.setFavorite(false);
                    return userRelationsWithEvent.getUserRelations();
                })
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] Event with id: " + eventId + " not found.");
                    return new EventNotFoundException("Event with id: " + eventId + " not found.");
                });

        userRepository.save(user);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
    }

    @Transactional
    @Override
    public void saveEventToFavorite(Long userId, Long eventId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());
        User currentUser = userRepository.findUserById(userId)
                .orElseThrow(() -> {
                    String message = "[EXCEPTION] User with id: " + userId + " not found.";
                    log.error(message);
                    return new UserNotFoundException(message);
                });

        User user = currentUser.getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEventRelations().getId().equals(eventId))
                .findFirst()
                .map(userRelationsWithEvent -> {
                    userRelationsWithEvent.setFavorite(true);
                    return userRelationsWithEvent.getUserRelations();
                })
                .orElseGet(() -> {
                    UserRelationsWithEvent userRelationsWithEvent = UserRelationsWithEvent
                            .builder()
                            .isFavorite(true).build();
                    userRelationsWithEvent.setEventRelations(eventRepository.findById(eventId)
                            .orElseThrow(() -> {
                                log.error("[EXCEPTION] event with id {}, does not exist", eventId);
                                return new EventNotFoundException(String.format("Event with id %s  does not exist", eventId));
                            }));
                    userRelationsWithEvent.setUserRelations(currentUser);
                    userRelationsWithEventService.saveUserRelationsWithEvent(userRelationsWithEvent);
                    return userRelationsWithEvent.getUserRelations();
                });

        userRepository.save(user);

        log.info("[TRANSACTION] Close transaction in class: " + this.getClass().getName());
    }

    private User findUserById(Long userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] User with id: " + userId + " not found.");
                    return new UserNotFoundException("User with id: " + userId + " not found.");
                });
    }
}
