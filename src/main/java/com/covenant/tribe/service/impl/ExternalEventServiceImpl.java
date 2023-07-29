package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.EventCategory;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.TagRepository;
import com.covenant.tribe.repository.UserRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    Set<String> EXTRA_KUDA_GO_CATEGORIES;
    Map<String, String> CATEGORY_NAMES_FOR_MATCHING;

    String EXTERNAL_EVENT_ORGANIZER_NAME;

    public ExternalEventServiceImpl(EventRepository eventRepository, UserRepository userRepository, EventAddressMapper eventAddressMapper, EventTypeRepository eventTypeRepository, TagRepository tagRepository, EventMapper eventMapper, EventAvatarMapper eventAvatarMapper) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.eventAddressMapper = eventAddressMapper;
        this.eventTypeRepository = eventTypeRepository;
        this.tagRepository = tagRepository;
        this.eventMapper = eventMapper;
        this.eventAvatarMapper = eventAvatarMapper;
    }

    @PostConstruct
    public void init() {
        EXTRA_KUDA_GO_CATEGORIES = Set.of(EventCategory.STOCKS.getKudaGoName());
        CATEGORY_NAMES_FOR_MATCHING = getCategoryNamesForMatching();
        EXTERNAL_EVENT_ORGANIZER_NAME = "Tribe";
    }

    @Override
    public List<KudagoEventDto> prepareEventsForCreating(
            Map<Long, KudagoEventDto> kudaGoEvents,
            int daysQuantityToFirstPublication
    ) {
        List<Long> existingEventIds = eventRepository.findAllRepeatableEventIds(
                kudaGoEvents.keySet(), List.of(LocalDate.now(), LocalDate.now().minusDays(100))
        );
        existingEventIds.forEach(kudaGoEvents::remove);
        List<KudagoEventDto> filteredEvents = filterEvents(kudaGoEvents, daysQuantityToFirstPublication);
        List<KudagoEventDto> eventsAfterDeletingStartDates = deleteExpiredStartDates(filteredEvents);
        List<KudagoEventDto> eventsAfterDeletingEventsWithoutDates = deleteEventsWithoutDates(eventsAfterDeletingStartDates);
        List<KudagoEventDto> eventsAfterDeletingExtraCategories = deleteExtraCategories(eventsAfterDeletingEventsWithoutDates);
        List<KudagoEventDto> eventsAfterIsFreeFieldChecking = fillBlankIsFreeFields(eventsAfterDeletingExtraCategories);
        return changeKudaGoCategoriesToTribeCategories(eventsAfterIsFreeFieldChecking);
    }

    private List<KudagoEventDto> fillBlankIsFreeFields(List<KudagoEventDto> eventsAfterDeletingExtraCategories) {
        eventsAfterDeletingExtraCategories.forEach(event -> {
            if (event.getIsFree() == null) {
                event.setIsFree(false);
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
            Map<Long, ReverseGeocodingData> reverseGeocodingData,
            Map<Long, List<String>> imageFileNames,
            Map<Long, List<Long>> eventTagIds,
            Map<Long, ExternalEventDates> externalEventDates
    ) {
        kudaGoEvents.forEach(kudagoEvent -> {
            User organizer = getExternalEventOrganizer();
            EventAddress eventAddress = eventAddressMapper.matToEventAddress(
                    reverseGeocodingData, kudagoEvent.getId()
            );
            Set<EventAvatar> eventImages = eventAvatarMapper.mapToEventAvatars(
                imageFileNames.get(kudagoEvent.getId())
            );
            EventType eventType = getEventTypeByName(kudagoEvent.getCategories().get(0));
            List<Tag> eventTags = tagRepository.findByIdIn(eventTagIds.get(kudagoEvent.getId()));
            UserRelationsWithEvent userRelationsWithEvent = UserRelationsWithEvent.builder()
                    .userRelations(organizer)
                    .isInvited(false)
                    .isParticipant(true)
                    .isWantToGo(false)
                    .isViewed(false)
                    .isFavorite(false)
                    .build();
            ExternalEventDates dates = externalEventDates.get(kudagoEvent.getId());
            Boolean hasAgeRestriction = hasAgeRestriction(kudagoEvent.getAgeRestriction());

            Event newEventFromKudaGo = eventMapper.mapToEvent(
                    kudagoEvent, organizer, eventAddress, eventType,
                    eventTags, userRelationsWithEvent, dates, hasAgeRestriction, eventImages
            );
            eventRepository.save(newEventFromKudaGo);
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
        if (event.getCategories().isEmpty()) {
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
        if (event.getDates() == null || event.getDates().isEmpty()) {
            log.error("Event with id: {} has no dates", event.getId());
            return false;
        }
        if (event.getBodyText() == null) {
            log.error("Event with id: {} has no boy text", event.getId());
            return false;
        }
        if (event.getAgeRestriction() == null) {
            log.error("Event with id: {} has no age restriction", event.getId());
            return false;
        }
        if (event.getLocation().getCoords().getLat() == null || event.getLocation().getCoords().getLon() == null) {
            log.error("Event with id: {} has no coords", event.getId());
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

    private List<KudagoEventDto> filterEvents(Map<Long, KudagoEventDto> events, int daysQuantityToFirstPublication) {
        return events.values().stream()
                .filter(kudagoEvent -> {
                   return kudagoEvent
                            .getPublicationDate()
                            .isAfter(LocalDate.now().minusDays(daysQuantityToFirstPublication));
                })
                .filter(event -> {
                    return checkEventsForRequiredFields(event);
                })
                .filter(kudagoEventDto -> {
                    return kudagoEventDto.getDates() != null && !kudagoEventDto.getDates().isEmpty();
                })
                .toList();
    }


}
