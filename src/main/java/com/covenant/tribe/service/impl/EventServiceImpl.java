package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.QUserRelationsWithEvent;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.*;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.user.UserStatus;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import com.covenant.tribe.exeption.UnexpectedDataException;
import com.covenant.tribe.exeption.event.*;
import com.covenant.tribe.exeption.scheduling.BroadcastNotFoundException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.message.MessageStrategyName;
import com.covenant.tribe.scheduling.model.Broadcast;
import com.covenant.tribe.scheduling.model.BroadcastEntity;
import com.covenant.tribe.scheduling.notifications.BroadcastRepository;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import com.covenant.tribe.scheduling.service.SchedulerService;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.FirebaseService;
import com.covenant.tribe.service.TagService;
import com.covenant.tribe.service.UserRelationsWithEventService;
import com.covenant.tribe.service.UserService;
import com.covenant.tribe.util.mapper.*;
import com.covenant.tribe.util.querydsl.EventFilter;
import com.covenant.tribe.util.querydsl.PartsOfDay;
import com.covenant.tribe.util.querydsl.QPredicates;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {

    EventTypeRepository eventTypeRepository;
    EventRepository eventRepository;
    TagRepository tagRepository;
    BroadcastRepository broadcastRepository;
    TagService tagService;
    SchedulerService schedulerService;
    EventAvatarRepository eventAvatarRepository;
    UserRepository userRepository;
    FileStorageRepository fileStorageRepository;
    FirebaseService firebaseService;
    UserRelationsWithEventRepository userRelationsWithEventRepository;
    UserRelationsWithEventService userRelationsWithEventService;
    UserService userService;
    EventMapper eventMapper;
    EventTypeMapper eventTypeMapper;
    EventTagMapper eventTagMapper;
    UserMapper userMapper;
    EventAddressMapper eventAddressMapper;
    Environment environment;

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Page<SearchEventDTO> getEventsByFilter(EventFilter filter, Long currentUserId, Integer page, Integer pageSize) {
        if (filter.getStrictEventSort() == null) filter.setStrictEventSort(false);
        QPredicates qPredicates = QPredicates.builder();
        QSort orders = new QSort();
        qPredicates.add(Boolean.TRUE, QEvent.event.showEventInSearch.isTrue());
        qPredicates.add(EventStatus.PUBLISHED, QEvent.event.eventStatus.eq(EventStatus.PUBLISHED));
        boolean filterPresent = filter.getSort() != null;
        boolean distanceFilter = filterPresent && filter.getSort().equals(EventSort.DISTANCE);
        orders = handleDistance(filter, qPredicates, orders, distanceFilter);
        orders = handleTime(filter, qPredicates, orders, filterPresent);
        orders = handleAlco(filter, qPredicates, orders, filterPresent);
        qPredicates.add(filter.getIsFree(), QEvent.event.isFree::eq);
        qPredicates.add(filter.getIsEighteenYearLimit(), QEvent.event.isEighteenYearLimit::eq);
        handleText(filter, qPredicates);
        Pageable pageable = QPageRequest.of(page, pageSize, orders);
        Predicate predicate = qPredicates.build();
        Page<Event> filteredEvents = eventRepository.findAll(predicate, pageable);
        if (currentUserId != null) {
            List<UserRelationsWithEvent> eventsCurrentUser = getUserRelationsWithEvents(currentUserId);
            return filteredEvents.map(event -> eventMapper.mapToSearchEventDTO(event, eventsCurrentUser));
        }
        return filteredEvents.map(eventMapper::mapToSearchEventDTO);
    }

    private void handleText(EventFilter filter, QPredicates qPredicates) {
        qPredicates.add(filter.getText(), (t) ->
                QEvent.event.eventName.containsIgnoreCase(t)
                        .or(QEvent.event.eventDescription.containsIgnoreCase(t))
                        .or(QEvent.event.tagList.any().tagName.contains(t)));
    }

    private List<UserRelationsWithEvent> getUserRelationsWithEvents(Long currentUserId) {
        List<UserRelationsWithEvent> eventsCurrentUser = userRepository.findUserByIdAndStatus(
                        currentUserId, UserStatus.ENABLED)
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] User with id %s, dont exist", currentUserId
                    );
                    log.error(message);
                    return new UserNotFoundException(message);
                }).getUserRelationsWithEvents();
        return eventsCurrentUser;
    }

    private QSort handleDistance(EventFilter filter, QPredicates qPredicates, QSort orders, boolean distanceFilter) {
        if (filter.getLongitude() != null && filter.getLatitude() != null) {
            NumberExpression<Double> distance = Expressions.numberTemplate(
                    Double.class,
                    "ST_Distance(" + QEvent.event.eventAddress.eventPosition +
                            ", ST_MakePoint(" + filter.getLongitude() + ", " + filter.getLatitude() + "))");

            qPredicates.add(filter.getDistanceInMeters(), distance::lt);

            if (distanceFilter) {
                orders = getOrder(filter, distance);
            }
        } else if (distanceFilter) {
            throw new EventSortingException("Position is not specified for sorting by distance");
        }
        return orders;
    }

    private QSort handleTime(EventFilter filter, QPredicates qPredicates, QSort orders, boolean filterPresent) {
        DateTimePath<OffsetDateTime> startTime = QEvent.event.startTime;
        DateTimePath<OffsetDateTime> endTime = QEvent.event.endTime;
        if (filterPresent && filter.getSort().equals(EventSort.DATE)) {
            orders = getOrder(filter, startTime);
        }
        if (filter.getEventTypeId() != null) {
            qPredicates.add(filter.getEventTypeId(), QEvent.event.eventType.id::in);
        }
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            OffsetDateTime startDateTime = filter.getStartDate().atTime(LocalTime.MIN).atOffset(ZoneOffset.UTC);
            OffsetDateTime endDateTime = filter.getEndDate().atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);

            qPredicates.add(startDateTime, startTime::after);
            qPredicates.add(endDateTime, endTime::before);
        }
        if (filter.getNumberOfParticipantsMin() != null && filter.getNumberOfParticipantsMax() != null) {
            QUserRelationsWithEvent qUserRelationsWithEvent = QUserRelationsWithEvent.userRelationsWithEvent;

            qPredicates.add(filter.getNumberOfParticipantsMin(), m -> {
                        JPQLQuery<Long> where = JPAExpressions.select(qUserRelationsWithEvent.count())
                                .from(qUserRelationsWithEvent)
                                .where(qUserRelationsWithEvent.isParticipant.isTrue())
                                .where(qUserRelationsWithEvent.eventRelations.id.eq(QEvent.event.id));
                        return where.goe(Long.valueOf(filter.getNumberOfParticipantsMin()));
                    }
            );
            qPredicates.add(filter.getNumberOfParticipantsMax(), m -> {
                        JPQLQuery<Long> where = JPAExpressions.select(qUserRelationsWithEvent.count())
                                .from(qUserRelationsWithEvent)
                                .where(qUserRelationsWithEvent.isParticipant.isTrue())
                                .where(qUserRelationsWithEvent.eventRelations.id.eq(QEvent.event.id));
                        return where.loe(Long.valueOf(filter.getNumberOfParticipantsMax()));
                    }
            );
        }
        String partsOfDay = filter.getPartsOfDay();
        QEventPartOfDay eventPartOfDay = QEventPartOfDay.eventPartOfDay;
        QEvent event = QEvent.event;
        if (filter.getStrictEventSort()) {
            qPredicates.add(partsOfDay, (p) -> {
                Set<String> partsSet = getStrings(partsOfDay);
                String join = String.join(", ", partsSet);
                JPQLQuery<Long> notStrict = getNotStrict(join, eventPartOfDay, event);
                JPQLQuery<Long> strict = notStrict //event has all parts of day in partSet
                        .having(eventPartOfDay.partsOfDay.countDistinct()
                                .eq(Expressions.asNumber(partsSet.size())));
                JPQLQuery<Long> mainQuery = JPAExpressions.select(event.id) //event has only those parts of day which are in partSet
                        .from(event)
                        .join(event.partsOfDay, eventPartOfDay)
                        .where(event.id.in(strict)).groupBy(event.id)
                        .having(eventPartOfDay.id.countDistinct().eq(Expressions.asNumber(partsSet.size())));
                BooleanExpression in = event.id
                        .in(mainQuery);
                return in;
            });
        } else {
            qPredicates.add(partsOfDay, (p) -> {
                Set<String> partsSet = getStrings(partsOfDay);
                String join = String.join(", ", partsSet);
                JPQLQuery<Long> notStrict = getNotStrict(join, eventPartOfDay, event);
                BooleanExpression in = event.id
                        .in(notStrict);
                return in;
            });
        }

        if (filter.getDurationEventInHoursMin() != null && filter.getDurationEventInHoursMax() != null) {
            Predicate eventDurationInHours =
                    endTime.hour().subtract(startTime.hour()).goe(filter.getDurationEventInHoursMin())
                            .and(
                                    endTime.hour().subtract(startTime.hour()).loe(filter.getDurationEventInHoursMax())
                            );

            qPredicates.add(filter.getDurationEventInHoursMin(), eventDurationInHours);
        }
        return orders;
    }

    private Set<String> getStrings(String partsOfDay) {
        String[] partsOfDaySplit = partsOfDay.trim().replaceAll(" ", "").split(",");
        Set<String> partsSet = new HashSet<>();
        try {
            for (String part : partsOfDaySplit) {
                PartsOfDay partE = PartsOfDay.valueOf(part);
                partsSet.add(String.valueOf(partE.ordinal()));
            }
        } catch (IllegalArgumentException e) {
            throw new WrongPartOfADayFilter("Part of day filter is incorrect");
        }
        return partsSet;
    }

    private JPQLQuery<Long> getNotStrict(String join, QEventPartOfDay eventPartOfDay, QEvent event) {  //event has any part of day in partSet
        return JPAExpressions.select(event.id)
                .from(eventPartOfDay)
                .join(eventPartOfDay.event, event)
                .where(Expressions.booleanTemplate(eventPartOfDay.partsOfDay + " in (" + join + ")"))
                .groupBy(event.id);
    }

    private QSort handleAlco(EventFilter filter, QPredicates qPredicates, QSort orders, boolean filterPresent) {
        BooleanPath isPresenceOfAlcohol = QEvent.event.isPresenceOfAlcohol;
        if (filter.getIsPresenceOfAlcohol() != null) {
            qPredicates.add(filter.getIsPresenceOfAlcohol(), isPresenceOfAlcohol::eq);

        }
        if (filterPresent && filter.getSort().equals(EventSort.ALCOHOL)) {
            orders = getOrder(filter, isPresenceOfAlcohol);
        }
        return orders;
    }


    private QSort getOrder(EventFilter filter, ComparableExpressionBase expression) {
        if (filter.getOrder() == null || filter.getOrder().equals(SortOrder.ASC)) {
            return new QSort(expression.asc());
        }
        return new QSort(expression.desc());
    }


    @Transactional(readOnly = true)
    public DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        Event event = getEventById(eventId);
        checkEventStatus(event);

        DetailedEventInSearchDTO detailedEventInSearchDTO = null;

        if (userId != null) {
            User currentUser = userRepository
                    .findUserByIdAndStatus(userId, UserStatus.ENABLED)
                    .orElseThrow(() -> {
                        String message = String.format(
                                "User with id %s didn't found", userId
                        );
                        log.error(message);
                        return new UserNotFoundException(message);
                    });
            detailedEventInSearchDTO = eventMapper
                    .mapToDetailedEvent(event, currentUser);
        } else {
            detailedEventInSearchDTO = eventMapper
                    .mapToDetailedEvent(event, null);
        }

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return detailedEventInSearchDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventInUserProfileDTO> findEventsByOrganizerId(String organizerId, Long requestUserId) {
        Long organizerIdLong = Long.parseLong(organizerId);
        if (organizerIdLong.equals(requestUserId)) {
            return eventRepository.findAllByOrganizerIdAndEventStatusIsNot(
                            organizerIdLong, EventStatus.DELETED
                    )
                    .stream()
                    .map(event -> eventMapper.mapToEventInUserProfileDTO(event, organizerIdLong))
                    .toList();
        } else {
            return eventRepository
                    .findAllByOrganizerIdAndEventStatusIs(organizerIdLong, EventStatus.PUBLISHED)
                    .stream()
                    .map(event -> eventMapper.mapToEventInUserProfileDTO(event, organizerIdLong))
                    .toList();
        }

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

    @Transactional
    @Override
    public Event saveNewEvent(Event event) {
        if (eventRepository.findByEventNameAndStartTimeAndOrganizerId(
                event.getEventName(), event.getStartTime(), event.getOrganizer().getId()).isEmpty()) {

            return eventRepository.save(event);
        } else {
            String message = String.format(
                    "[EXCEPTION] Event with name %s and start time %s already exist",
                    event.getEventName(), event.getStartTime());
            throw new EventAlreadyExistException(message);
        }
    }

    @Transactional
    @Override
    public Event saveNewExternalEvent(List<KudagoEventDto> externalEvents, Map<Long, ReverseGeocodingData> reverseGeocodingData, Map<Long, List<String>> images) {
        for (KudagoEventDto externalEvent : externalEvents) {

        }
        return null;
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] event with id {}, does not exist", eventId);
                    return new EventNotFoundException(String.format("Event with id %s  does not exist", eventId));
                });
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
        sendNecessaryNotification(event);
        OffsetDateTime hourNotificationSendTime = event.getStartTime().minusHours(1);
        MessageStrategyName messageStrategyName = null;
        if (Objects.equals(environment.getProperty("spring.profiles.active"), "dev")
                || Objects.equals(environment.getProperty("spring.profiles.active"), "test")) {
            messageStrategyName = MessageStrategyName.CONSOLE;
        } else {
            messageStrategyName = MessageStrategyName.FIREBASE;
        }
        Broadcast broadcast = Broadcast.builder()
                .subjectId(event.getId())
                .repeatDate(hourNotificationSendTime)
                .endDate(event.getEndTime())
                .notificationStrategyName(NotificationStrategyName.EVENT)
                .status(BroadcastStatuses.NEW)
                .messageStrategyName(messageStrategyName)
                .build();
        try {
            if (event.isStartTimeUpdated()) {
                schedulerService.updateTriggerTime(broadcast);
                event.setStartTimeUpdated(false);
                eventRepository.save(event);
            } else {
                schedulerService.schedule(broadcast);
            }
        } catch (SchedulerException e) {
            String message = String.format(
                    "Cannot schedule broadcast: %s for event with id %s",
                    broadcast.toString(), event.getId()
            );
            log.error(message);
        }
    }

    private void sendNecessaryNotification(Event event) {

        if (event.isSendToAllUsersByInterests()) {
            List<User> allUsersIdWhoInterestingEventType = userService
                    .findAllByInterestingEventTypeContaining(event.getEventType().getId()).stream()
                    .filter(u -> !u.getId().equals(event.getOrganizer().getId()))
                    .toList();

            firebaseService.sendNotificationsToUsers(allUsersIdWhoInterestingEventType,
                    event.isEighteenYearLimit(),
                    "Some title",
                    "Текст приглашения нужно придумать, id мероприятия лежит в поле data",
                    event.getId());
        }
        List<Long> invitedUserIds = event.getEventRelationsWithUser().stream()
                .filter(u -> u.isInvited())
                .map(u -> u.getUserRelations().getId())
                .toList();
        if (!invitedUserIds.isEmpty()) {
            List<User> usersWhoInvited = userService
                    .findAllById(invitedUserIds.stream().toList()).stream()
                    .filter(u -> !u.getId().equals(event.getOrganizer().getId()))
                    .toList();

            firebaseService.sendNotificationsToUsers(usersWhoInvited,
                    event.isEighteenYearLimit(),
                    "Some title",
                    "Текст приглашения нужно придумать, id мероприятия лежит в поле data",
                    event.getId());
        }
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
                            userId, eventId);
                    log.error(message);
                    return new UserRelationsWithEventNotFoundException(message);
                });
        userRelationsWithEvent.setParticipant(true);
        userRelationsWithEvent.setInvited(false);
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
                            userId, eventId);
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
                .findByUserRelationsIdAndEventRelationsIdAndIsParticipantTrue(Long.parseLong(userId), eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "[EXCEPTION] User relations with id %s and event relations with id %s does not exist",
                            userId, eventId);
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

        Optional<BroadcastEntity> broadcastEntityOpt = broadcastRepository
                .findBySubjectIdAndStatusNot(eventId, BroadcastStatuses.COMPLETE_SUCCESSFULLY);

        if (broadcastEntityOpt.isPresent()) {
            BroadcastEntity broadcastEntity = broadcastEntityOpt.get();
            broadcastEntity.setStatus(BroadcastStatuses.CANCELLED);
            TriggerKey triggerKey = new TriggerKey(broadcastEntity.getTriggerKey());
            schedulerService.unschedule(triggerKey);
            broadcastRepository.save(broadcastEntity);
        }

    }

    @Transactional
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
        UserRelationsWithEvent userRelationsWithEvent = null;
        Optional<UserRelationsWithEvent> userRelationsWithEventOptional = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsId(Long.parseLong(userId), eventId);
        if (userRelationsWithEventOptional.isPresent()) {
            userRelationsWithEvent = userRelationsWithEventOptional.get();
            userRelationsWithEvent.setWantToGo(true);
        } else {
            userRelationsWithEvent = UserRelationsWithEvent.builder()
                    .isInvited(false)
                    .isParticipant(false)
                    .isWantToGo(true)
                    .isFavorite(false)
                    .build();
        }
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

    @Transactional
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
                .findUserByIdAndStatus(Long.parseLong(userId), UserStatus.ENABLED)
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

    @Override
    public EventDto getEvent(Long eventId, Long organizerId) { //TODO Проверить как работае т для приватного события
        Event event = getEventById(eventId);
        EventTypeInfoDto eventTypeInfoDto = eventTypeMapper
                .mapToEventTypeInfoDto(event.getEventType());
        List<String> avatarUrlList = event.getEventAvatars().stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();
        EventAddressDTO eventAddressDTO = eventAddressMapper.mapToEventAddressDTO(
                event.getEventAddress()
        );
        List<EventTagDTO> eventTagDtoList = eventTagMapper.mapEventTagListToEventTagDtoList(
                event.getTagList()
        );

        List<UserToSendInvitationDTO> invitedAndParticipatedUserList = event.getEventRelationsWithUser().stream()
                .filter(userRelationsWithEvent -> {
                    return userRelationsWithEvent.isInvited() || userRelationsWithEvent.isParticipant();
                })
                .map(UserRelationsWithEvent::getUserRelations)
                .map(userMapper::mapToUserToSendInvitationDTO)
                .toList();

        return EventDto.builder()
                .eventTypeInfoDto(eventTypeInfoDto)
                .avatarUrls(avatarUrlList)
                .name(event.getEventName())
                .addressDTO(eventAddressDTO)
                .startDateTime(event.getStartTime())
                .endDateTime(event.getEndTime())
                .tags(eventTagDtoList)
                .description(event.getEventDescription())
                .invitations(invitedAndParticipatedUserList)
                .isPrivate(event.isPrivate())
                .isShowInSearch(event.isShowEventInSearch())
                .isSendByInterests(event.isSendToAllUsersByInterests())
                .isEighteenYearLimit(event.isEighteenYearLimit())
                .build();
    }

    @Transactional
    @Override
    public DetailedEventInSearchDTO updateEvent(UpdateEventDto updateEventDto) throws IOException {
        Event eventForUpdate = findEventById(updateEventDto.getEventId());
        ArrayList<String> avatarsForDeletingFromTempDirectory = new ArrayList<>();
        ArrayList<String> avatarsForDeletingFromDb = new ArrayList<>();

        if (updateEventDto.getEventTypeId().longValue() != eventForUpdate.getEventType().getId()) {
            EventType newEventType = eventTypeRepository
                    .findById(updateEventDto.getEventTypeId())
                    .orElseThrow(() -> {
                        String message = String.format(
                                "EventType with id %s didn't found", updateEventDto.getEventTypeId()
                        );
                        log.error(message);
                        return new EventTypeNotFoundException(message);
                    });
            eventForUpdate.setEventType(newEventType);
        }

        if (!updateEventDto.getAvatarsForDeleting().isEmpty()) {
            updateEventDto.getAvatarsForDeleting().forEach(avatarUrl -> {
                        if (avatarUrl.contains("/")) {
                            avatarsForDeletingFromDb.add(avatarUrl);
                        } else {
                            avatarsForDeletingFromTempDirectory.add(avatarUrl);
                        }
                    }
            );
            eventAvatarRepository.deleteAllByAvatarUrlIn(avatarsForDeletingFromDb);
        }

        if (!updateEventDto.getAvatarsForAdding().isEmpty()) {
            List<String> avatarsUrlsForDb = fileStorageRepository.addEventAvatars(
                    updateEventDto.getAvatarsForAdding()
            );
            avatarsUrlsForDb.forEach(avatarUrl -> {
                EventAvatar eventAvatar = EventAvatar.builder()
                        .event(eventForUpdate)
                        .avatarUrl(avatarUrl)
                        .build();
                eventForUpdate.addEventAvatar(eventAvatar);
            });
            avatarsForDeletingFromTempDirectory.addAll(updateEventDto.getAvatarsForAdding());
        }

        if (!updateEventDto.getName().equals(eventForUpdate.getEventName())) {
            eventForUpdate.setEventName(updateEventDto.getName());
        }

        EventAddressDTO addressDto = updateEventDto.getAddressDTO();
        EventAddress eventAddress = eventForUpdate.getEventAddress();

        if (addressDto.getEventLongitude().compareTo(eventAddress.getEventLongitude()) != 0) {
            eventAddress.setEventLongitude(addressDto.getEventLongitude());
        }
        if (addressDto.getEventLatitude().compareTo(eventAddress.getEventLatitude()) != 0) {
            eventAddress.setEventLatitude(addressDto.getEventLatitude());
        }
        if (!addressDto.getCity().equals(eventAddress.getCity())) {
            eventAddress.setCity(addressDto.getCity());
        }
        if (!addressDto.getRegion().equals(eventAddress.getRegion())) {
            eventAddress.setRegion(addressDto.getRegion());
        }
        if (!addressDto.getStreet().equals(eventAddress.getStreet())) {
            eventAddress.setStreet(addressDto.getStreet());
        }
        if (!addressDto.getDistrict().equals(eventAddress.getDistrict())) {
            eventAddress.setDistrict(addressDto.getDistrict());
        }
        if (!addressDto.getBuilding().equals(eventAddress.getBuilding())) {
            eventAddress.setBuilding(addressDto.getBuilding());
        }
        if (!addressDto.getHouseNumber().equals(eventAddress.getHouseNumber())) {
            eventAddress.setHouseNumber(addressDto.getHouseNumber());
        }
        if (!addressDto.getFloor().equals(eventAddress.getFloor())) {
            eventAddress.setFloor(addressDto.getFloor());
        }

        if (!updateEventDto.getStartDateTime().isEqual(eventForUpdate.getStartTime())) {
            eventForUpdate.setStartTimeUpdated(true);
            eventForUpdate.setStartTime(updateEventDto.getStartDateTime());
            eventForUpdate.setPartsOfDay(eventMapper.partEnumSetToEntity(eventMapper.getPartsOfDay(eventForUpdate)));
        }

        if (!updateEventDto.getEndDateTime().isEqual(eventForUpdate.getEndTime())) {
            eventForUpdate.setEndTime(updateEventDto.getEndDateTime());
            eventForUpdate.setPartsOfDay(eventMapper.partEnumSetToEntity(eventMapper.getPartsOfDay(eventForUpdate)));
        }

        if (!updateEventDto.getTagIdsForDeleting().isEmpty()) {
            List<Tag> tagsForDeleting = tagRepository.findByIdIn(updateEventDto.getTagIdsForDeleting());
            eventForUpdate.getTagList().removeAll(tagsForDeleting);
        }

        if (!updateEventDto.getTagIdsForAdding().isEmpty()) {
            List<Tag> tagsForAdding = tagRepository.findByIdIn(updateEventDto.getTagIdsForAdding());
            eventForUpdate.getTagList().addAll(tagsForAdding);
        }

        if (!updateEventDto.getNewTags().isEmpty()) {
            List<Tag> newTags = tagService.saveAll(updateEventDto.getNewTags());
            eventForUpdate.getTagList().addAll(newTags);
            EventType eventType = eventForUpdate.getEventType();
            eventType.addTags(newTags);
            eventTypeRepository.save(eventType);
        }

        if (!updateEventDto.getDescription().equals(eventForUpdate.getEventDescription())) {
            eventForUpdate.setEventDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.isEighteenYearLimit() != eventForUpdate.isEighteenYearLimit()) {
            eventForUpdate.setEighteenYearLimit(updateEventDto.isEighteenYearLimit());
        }

        if (!updateEventDto.getParticipantIdsForAdding().isEmpty()) {
            List<User> usersWhoSendNotification = new ArrayList<>();
            updateEventDto.getParticipantIdsForAdding().forEach(participantId -> {
                User participant = findUserById(participantId);
                UserRelationsWithEvent userRelationsWithEvent =
                        userRelationsWithEventRepository.findByUserRelationsIdAndEventRelationsId(
                                participantId, eventForUpdate.getId()
                        ).orElseGet(() -> {
                            return
                                    UserRelationsWithEvent.builder()
                                            .userRelations(participant)
                                            .eventRelations(eventForUpdate)
                                            .isInvited(true)
                                            .isParticipant(false)
                                            .isWantToGo(false)
                                            .isFavorite(false)
                                            .build();
                        });
                userRelationsWithEventRepository.save(userRelationsWithEvent);
                usersWhoSendNotification.add(participant);
            });
        }

        if (!updateEventDto.getParticipantIdsForDeleting().isEmpty()) {
            List<User> usersWhoSendNotification = new ArrayList<>();
            updateEventDto.getParticipantIdsForDeleting().forEach(participantId -> {
                User participant = findUserById(participantId);
                UserRelationsWithEvent userRelationsWithEvent =
                        userRelationsWithEventRepository.findByUserRelationsIdAndEventRelationsId(
                                participantId, eventForUpdate.getId()
                        ).orElseThrow(() -> {
                            String message = String.format(
                                    "User with id: %s don't know event with id: %s"
                                    , participant.getId(), eventForUpdate.getId());
                            log.error(message);
                            return new UserRelationsWithEventNotFoundException(message);
                        });
                userRelationsWithEvent.setParticipant(false);
                userRelationsWithEvent.setInvited(false);
                userRelationsWithEvent.setWantToGo(false);
                userRelationsWithEventRepository.save(userRelationsWithEvent);
                usersWhoSendNotification.add(participant);
            });
            firebaseService.sendNotificationsToUsers(usersWhoSendNotification,
                    eventForUpdate.isEighteenYearLimit(),
                    "Some title",
                    "Текст отказа нужно придумать, id мероприятия лежит в поле data",
                    eventForUpdate.getId());
        }

        if (updateEventDto.isPrivate() != eventForUpdate.isPrivate()) {
            eventForUpdate.setPrivate(updateEventDto.isPrivate());
        }

        if (updateEventDto.isShowInSearch() != eventForUpdate.isShowEventInSearch()) {
            eventForUpdate.setShowEventInSearch(updateEventDto.isShowInSearch());
        }

        if (updateEventDto.isSendByInterests() && !eventForUpdate.isSendToAllUsersByInterests()) {
            eventForUpdate.setSendToAllUsersByInterests(true);
            List<User> usersWhoSendNotification =
                    userRepository.findAllByInterestingEventTypeContainingAndStatus(
                            eventForUpdate.getEventType().getId(), UserStatus.ENABLED.toString()
                    );
        }

        if (updateEventDto.isHasAlcohol() != eventForUpdate.isPresenceOfAlcohol()) {
            eventForUpdate.setPresenceOfAlcohol(updateEventDto.isHasAlcohol());
        }
        eventForUpdate.setEventStatus(EventStatus.VERIFICATION_PENDING);
        eventRepository.save(eventForUpdate);

        fileStorageRepository.deleteFileInTmpDir(avatarsForDeletingFromTempDirectory);
        fileStorageRepository.deleteEventAvatars(avatarsForDeletingFromDb);

        return eventMapper.mapToDetailedEvent(eventForUpdate, null);
    }

    @Transactional
    @Override
    public void updatePartsOfDay() {
        List<Event> all = eventRepository.findAll();
        eventRepository.saveAll(all.stream().map(e -> {
            Set<PartsOfDay> partsOfDay = eventMapper.getPartsOfDay(e);
            Set<EventPartOfDay> collect = partsOfDay.stream().map(eventMapper::partEnumToEntity).collect(Collectors.toSet());
            e.setPartsOfDay(collect);
            return e;
        }).toList());
    }

    @Transactional
    @Override
    public void withdrawalRequestToParticipateInPrivateEvent(Long eventId, Long userId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        Optional<UserRelationsWithEvent> userRelationsWithEventOptional = userRelationsWithEventRepository
                .findByUserRelationsIdAndEventRelationsId(userId, eventId);
        if (userRelationsWithEventOptional.isEmpty() || !userRelationsWithEventOptional.get().isWantToGo()) {
            String erMessage = """
                    User with id: %s is don't have a request to go in event with id: %s
                    """.formatted(userId, eventId);
            log.error(erMessage);
            throw new UnexpectedDataException(erMessage);
        }
        UserRelationsWithEvent userRelationsWithEvent = userRelationsWithEventOptional.get();
        if (userRelationsWithEvent.isParticipant()) {
            String erMessage = """
                    User with id: %s is already participant in event with id: %s
                    """.formatted(userId, eventId);
            log.error(erMessage);
            throw new UserAlreadyParticipantException(erMessage);
        }
        if (userRelationsWithEvent.isInvited()) {
            String erMessage = """
                    User with id: %s is already invited in event with id: %s
                    """.formatted(userId, eventId);
            log.error(erMessage);
            throw new UserAlreadyInvitedException(erMessage);
        }
        userRelationsWithEvent.setWantToGo(false);
        userRelationsWithEventRepository.save(userRelationsWithEvent);

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
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
        User currentUser = userRepository.findUserByIdAndStatus(userId, UserStatus.ENABLED)
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
        return userRepository.findUserByIdAndStatus(userId, UserStatus.ENABLED)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] User with id: " + userId + " not found.");
                    return new UserNotFoundException("User with id: " + userId + " not found.");
                });
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "Event with id %s didn't found", eventId
                    );
                    log.error(message);
                    return new EventNotFoundException(message);
                });
    }
}
