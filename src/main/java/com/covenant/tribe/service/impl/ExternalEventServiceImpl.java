package com.covenant.tribe.service.impl;

import com.covenant.tribe.chat.service.ChatService;
import com.covenant.tribe.client.kudago.dto.KudagoDate;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.*;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.EventCategory;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.*;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.ExternalEventDateService;
import com.covenant.tribe.service.ExternalEventService;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import com.covenant.tribe.util.mapper.EventAvatarMapper;
import com.covenant.tribe.util.mapper.EventMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventServiceImpl implements ExternalEventService {

    final EventRepository eventRepository;
    final UserRepository userRepository;
    final EventAddressMapper eventAddressMapper;
    final EventTypeRepository eventTypeRepository;
    final TagRepository tagRepository;
    final EventMapper eventMapper;
    final EventAvatarMapper eventAvatarMapper;
    final EventSearchService eventSearchService;
    final ExternalEventDateService externalEventDateService;

    final ChatService chatService;

    final EventAddressRepository eventAddressRepository;
    Set<String> EXTRA_KUDA_GO_CATEGORIES;
    Map<String, String> CATEGORY_NAMES_FOR_MATCHING;

    String EXTERNAL_EVENT_ORGANIZER_NAME;

    public ExternalEventServiceImpl(EventRepository eventRepository, UserRepository userRepository, EventAddressMapper eventAddressMapper, EventTypeRepository eventTypeRepository, TagRepository tagRepository, EventMapper eventMapper, EventAvatarMapper eventAvatarMapper, EventSearchService eventSearchService, ExternalEventDateService externalEventDateService, ChatService chatService, EventAddressRepository eventAddressRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventAddressMapper = eventAddressMapper;
        this.eventTypeRepository = eventTypeRepository;
        this.tagRepository = tagRepository;
        this.eventMapper = eventMapper;
        this.eventAvatarMapper = eventAvatarMapper;
        this.eventSearchService = eventSearchService;
        this.externalEventDateService = externalEventDateService;
        this.chatService = chatService;
        this.eventAddressRepository = eventAddressRepository;
    }

    @PostConstruct
    public void init() {
        EXTRA_KUDA_GO_CATEGORIES = Set.of(EventCategory.STOCKS.getKudaGoName());
        CATEGORY_NAMES_FOR_MATCHING = getCategoryNamesForMatching();
        EXTERNAL_EVENT_ORGANIZER_NAME = "Tribe";
    }

    @Override
    public List<KudagoEventDto> prepareEventsForCreating(
            Map<Long, KudagoEventDto> kudaGoEvents, LocalDateTime minPublicationDate
    ) {

        List<Long> existingEventIds = eventRepository.findAllRepeatableEventIds(
                minPublicationDate.toLocalDate(), kudaGoEvents.keySet());
        existingEventIds.forEach(kudaGoEvents::remove);
        List<KudagoEventDto> filteredEvents = filterEvents(kudaGoEvents);
        List<KudagoEventDto> eventsAfterDeletingStartDates = deleteExpiredStartDates(filteredEvents);
        List<KudagoEventDto> eventsAfterDeletingEventsWithoutDates = deleteEventsWithoutDates(eventsAfterDeletingStartDates);
        List<KudagoEventDto> eventsAfterDeletingExtraCategories = deleteExtraCategories(eventsAfterDeletingEventsWithoutDates);
        List<KudagoEventDto> eventsAfterDeletingTooLongEventNames = deleteTooLongEventNames(eventsAfterDeletingExtraCategories);
        List<KudagoEventDto> eventsAfterIsFreeFieldChecking = fillBlankIsFreeFields(eventsAfterDeletingTooLongEventNames);
        List<KudagoEventDto> eventsAfterDeletingRecurringEventNames = deleteRecurringEventNames(eventsAfterIsFreeFieldChecking);
        List<KudagoEventDto> eventsAfterDeletingDbEventsWithSameNameAndStartDate =
                deleteEventsWithSameStartDateAndName(eventsAfterDeletingRecurringEventNames);
        return changeKudaGoCategoriesToTribeCategories(eventsAfterDeletingDbEventsWithSameNameAndStartDate);
    }

    private List<KudagoEventDto> deleteEventsWithSameStartDateAndName(List<KudagoEventDto> eventsAfterIsFreeFieldChecking) {
        ArrayList<KudagoEventDto> eventsAfterDeletingDbEventsWithSameNameAndStartDate = new ArrayList<>();
        eventsAfterIsFreeFieldChecking.forEach(event -> {
            String eventName = event.getTitle();
            String timezone = event.getLocation().getTimezone();
            KudagoDate eventDate = event.getDates().get(0);
            LocalDateTime offsetStartTime = externalEventDateService.transformTimestampToLocalDateTime(
                    eventDate, timezone
            );
            Optional<Event> eventFromDb = eventRepository.findByEventNameAndStartTime(eventName, offsetStartTime);
            if (eventFromDb.isEmpty()) {
                eventsAfterDeletingDbEventsWithSameNameAndStartDate.add(event);
            }
        });
        return eventsAfterDeletingDbEventsWithSameNameAndStartDate;
    }
    private List<KudagoEventDto> deleteRecurringEventNames(List<KudagoEventDto> eventsAfterIsFreeFieldChecking) {
        Set<String> alreadyCheckEventNames = new HashSet<>();
        List<KudagoEventDto> eventsAfterDeletingRecurringEventNames = new ArrayList<>();
        eventsAfterIsFreeFieldChecking.forEach(event -> {
            if (!alreadyCheckEventNames.contains(event.getTitle())) {
                alreadyCheckEventNames.add(event.getTitle());
                eventsAfterDeletingRecurringEventNames.add(event);
            }
        });
        return eventsAfterDeletingRecurringEventNames;
    }

    private List<KudagoEventDto> deleteTooLongEventNames(List<KudagoEventDto> eventsAfterDeletingExtraCategories) {
        return eventsAfterDeletingExtraCategories.stream()
                .filter(event -> event.getTitle().length() < 100)
                .toList();
    }

    private List<KudagoEventDto> fillBlankIsFreeFields(List<KudagoEventDto> eventsAfterDeletingExtraCategories) {
        eventsAfterDeletingExtraCategories.forEach(event -> {
            if (event.getIsFree() == null) {
                event.setIsFree(false);
                log.info("Event with id: {} has no is free field", event.getId());
            }
            if (event.getAgeRestriction() == null) {
                event.setAgeRestriction("0");
                log.info("Event with id: {} has no age restriction", event.getId());
            }
        });
        return eventsAfterDeletingExtraCategories;
    }

    private List<KudagoEventDto> deleteEventsWithoutDates(List<KudagoEventDto> eventsAfterDeletingStartDates) {
        return eventsAfterDeletingStartDates.stream()
                .filter(event -> event.getDates() != null && !event.getDates().isEmpty())
                .toList();
    }

    private List<KudagoEventDto> deleteExtraCategories(List<KudagoEventDto> eventsAfterDeletingStartDates) {
        for (KudagoEventDto event : eventsAfterDeletingStartDates) {
            event.getCategories().removeIf(category -> EXTRA_KUDA_GO_CATEGORIES.contains(category));
        }
        return eventsAfterDeletingStartDates;
    }


    @Transactional
    @Override
    public void saveNewExternalEvents(
            List<KudagoEventDto> kudaGoEvents,
            Map<Long, List<EventContactInfo>> eventContactInfos,
            Map<Long, EventAddressDTO> eventAddresses,
            Map<Long, List<String>> imageFileNames,
            Map<Long, List<Long>> eventTagIds,
            Map<Long, ExternalEventDates> externalEventDates
    ) {
        kudaGoEvents.forEach(kudagoEvent -> {
            User organizer = getExternalEventOrganizer();
            EventAddressDTO eventAddressDTO = eventAddresses.get(kudagoEvent.getId());
            EventAddress eventAddress;
            List<EventAddress> addressesInDb =  eventAddressRepository.findByEventLatitudeAndEventLongitudeAndHouseNumberAndBuilding(
                    eventAddressDTO.getEventLatitude(),
                    eventAddressDTO.getEventLongitude(),
                    eventAddressDTO.getHouseNumber(),
                    eventAddressDTO.getBuilding()
            );
            if (addressesInDb.isEmpty()) {
                eventAddress = eventAddressMapper.mapToEventAddress(
                        eventAddresses, kudagoEvent.getId()
                );
                eventAddress = eventAddressRepository.save(eventAddress);
            } else {
                eventAddress = addressesInDb.get(0);
            }

            List<String> eventImagesFromMap = imageFileNames.get(kudagoEvent.getId());
            Set<EventAvatar> eventImages = Set.of();
            if (eventImagesFromMap.isEmpty()) {
                log.warn("Event with id: {} has no images", kudagoEvent.getId());
            } else {
                eventImages = eventAvatarMapper.mapToEventAvatars(
                        imageFileNames.get(kudagoEvent.getId())
                );
            }
            EventType eventType = getEventTypeByName(kudagoEvent.getCategories().get(0));
            List<Tag> eventTags = tagRepository.findByIdIn(eventTagIds.get(kudagoEvent.getId()));
            UserRelationsWithEvent userRelationsWithEvent = UserRelationsWithEvent.builder()
                    .userRelations(organizer)
                    .isInvited(false)
                    .isParticipant(true)
                    .isWantToGo(false)
                    .isFavorite(false)
                    .build();
            ExternalEventDates dates = externalEventDates.get(kudagoEvent.getId());
            Boolean hasAgeRestriction = hasAgeRestriction(kudagoEvent.getAgeRestriction());

            Event newEventFromKudaGo = eventMapper.mapToEvent(
                    kudagoEvent, organizer, eventAddress, eventType,
                    eventTags, userRelationsWithEvent, dates, hasAgeRestriction, eventImages
            );

            List<EventContactInfo> eventContactInfoList = eventContactInfos.get(kudagoEvent.getId());
            newEventFromKudaGo.addContactInfos(eventContactInfoList);
            Event save = eventRepository.save(newEventFromKudaGo);
            eventSearchService.create(save);
            chatService.createEventChat(save.getOrganizer(), save);
        });

    }
    private Boolean hasAgeRestriction(String ageRestriction) {
        if (ageRestriction == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(ageRestriction);
        if (matcher.find()) {
            int restriction = Integer.parseInt(matcher.group());
            return restriction >= 18;
        }
        return false;
    }

    private EventType getEventTypeByName(String name) {
        return eventTypeRepository.findEventTypeByTypeName(name).orElseThrow(
                () -> {
                    String erMessage = "EventType with name %s not found.".formatted(name);
                    log.error(erMessage);
                    return new EventTypeNotFoundException(erMessage);
                }
        );
    }

    private User getExternalEventOrganizer() {
        return userRepository
                .findUserByUsername(EXTERNAL_EVENT_ORGANIZER_NAME)
                .orElseThrow(() -> {
                    String erMessage = "[EXCEPTION]: External event organizer not found.";
                    log.error(erMessage);
                    return new UserNotFoundException(erMessage);
                });
    }

    private List<KudagoEventDto> deleteExpiredStartDates(List<KudagoEventDto> filteredEvents) {
        for (KudagoEventDto event : filteredEvents) {
            event.getDates().removeIf(
                    date -> date.getStartDate() == null || LocalDate.parse(date.getStartDate()).isBefore(LocalDate.now())
            );
        }
        return filteredEvents;
    }

    private List<KudagoEventDto> changeKudaGoCategoriesToTribeCategories(List<KudagoEventDto> events) {
        for (KudagoEventDto event : events) {
            List<String> categories = event.getCategories();
            String tribeCategory = CATEGORY_NAMES_FOR_MATCHING.get(categories.get(0));
            event.setCategories(List.of(tribeCategory));
        }
        return events;
    }

    private Map<String, String> getCategoryNamesForMatching() {
        Map<String, String> categoryNamesForMatching = new HashMap<>();
        for (EventCategory eventCategory : EventCategory.values()) {
            categoryNamesForMatching.put(eventCategory.getKudaGoName(), eventCategory.getTribeName());
        }
        return categoryNamesForMatching;
    }


    private boolean checkEventsForRequiredFields(KudagoEventDto event) {
        if (event.getCategories() == null || event.getCategories().isEmpty()) {
            log.error("Categories in kudaGoEvent with id: {} is empty", event.getId());
            return false;
        }
        if (event.getTitle() == null) {
            log.error("Name in kudaGoEvent with id: {} is null", event.getId());
            return false;
        }
        if (event.getLocation() == null) {
            log.error("Event with id: {} has no location", event.getId());
            return false;
        }
        if ((event.getPlace() == null)) {
            log.error("Event with id: {} has no places", event.getId());
            return false;
        }
        if (event.getPlace().getCoords().getLat() == null || event.getPlace().getCoords().getLon() == null) {
            log.error("Event with id: {} has no latitude or longitude", event.getId());
            return false;
        }
        if (event.getDates() == null || event.getDates().isEmpty()) {
            log.error("Event with id: {} has no dates", event.getId());
            return false;
        }
        if (event.getBodyText() == null) {
            log.error("Event with id: {} has no boy text", event.getId());
            return false;
        }
        return true;
    }

    private boolean checkForExtraCategories(List<String> kudaGoCategories) {
        for (String kudaGoCategoryName : kudaGoCategories) {
            if (EXTRA_KUDA_GO_CATEGORIES.contains(kudaGoCategoryName)) {
                return true;
            }
        }
        return false;
    }

    private List<KudagoEventDto> filterEvents(Map<Long, KudagoEventDto> events) {
        return events.values().stream()
                .filter(this::checkEventsForRequiredFields)
                .toList();
    }
}
