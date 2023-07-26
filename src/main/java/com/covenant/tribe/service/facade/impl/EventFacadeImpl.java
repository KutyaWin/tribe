package com.covenant.tribe.service.facade.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.service.*;
import com.covenant.tribe.service.facade.EventFacade;
import com.covenant.tribe.service.impl.pojo.CollectedDataForMappingToEvent;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import com.covenant.tribe.util.mapper.EventMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class EventFacadeImpl implements EventFacade {

    UserService userService;
    EventAddressMapper eventAddressMapper;
    EventTypeService eventTypeService;
    TagService tagService;
    EventMapper eventMapper;
    EventService eventService;
    PhotoStorageService photoStorageService;
    FirebaseService firebaseService;
    EventAvatarService eventAvatarService;


    @Transactional
    @Override
    public DetailedEventInSearchDTO handleNewEvent(
            RequestTemplateForCreatingEventDTO requestTemplateForCreatingEvent) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        CollectedDataForMappingToEvent resultCollectingData =
                collectDataFromDifferentServicesForMappingToEvent(requestTemplateForCreatingEvent);

        Event newEvent = eventMapper.mapToEvent(requestTemplateForCreatingEvent, resultCollectingData.organizer(),
                resultCollectingData.eventType(), resultCollectingData.eventAddress(),
                resultCollectingData.alreadyExistEventTags(), resultCollectingData.createdEventTagsByRequest(),
                resultCollectingData.invitedUserByRequest());

        newEvent = eventService.saveNewEvent(newEvent);

        processNecessaryPhotos(requestTemplateForCreatingEvent, newEvent);

        DetailedEventInSearchDTO responseEventDto = eventMapper
                .mapToDetailedEvent(eventService.getEventById(newEvent.getId()), newEvent.getOrganizer());

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return responseEventDto;
    }

    private CollectedDataForMappingToEvent collectDataFromDifferentServicesForMappingToEvent(
            RequestTemplateForCreatingEventDTO requestTemplateForCreatingEvent) {

        User organizer = userService.findUserByIdFetchUserAsOrganizer(requestTemplateForCreatingEvent.getOrganizerId());

        EventType eventType = eventTypeService
                .getEventTypeByIdFetchEventListWithTypeAndTagList(requestTemplateForCreatingEvent.getEventTypeId());

        EventAddress eventAddress = null;
        if (requestTemplateForCreatingEvent.getEventAddress() != null) {
            eventAddress = eventAddressMapper.mapToEventAddress(requestTemplateForCreatingEvent.getEventAddress());
        }

        List<Tag> alreadyExistEventTags = null;
        if (requestTemplateForCreatingEvent.getEventTagIds() != null) {
            alreadyExistEventTags = tagService
                    .findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(requestTemplateForCreatingEvent.getEventTagIds());
        }

        List<Tag> createdEventTagsByRequest = null;
        if (requestTemplateForCreatingEvent.getNewEventTagNames() != null) {
            createdEventTagsByRequest = tagService.saveAll(requestTemplateForCreatingEvent.getNewEventTagNames());
            eventType.addTags(createdEventTagsByRequest);
            eventTypeService.save(eventType);
        }

        List<User> invitedUserByRequest = null;
        if (requestTemplateForCreatingEvent.getInvitedUserIds() != null) {
            invitedUserByRequest = userService.findAllById(
                    requestTemplateForCreatingEvent.getInvitedUserIds().stream().toList()
            );
        }

        return new CollectedDataForMappingToEvent(organizer, eventType, eventAddress, alreadyExistEventTags,
                createdEventTagsByRequest, invitedUserByRequest);
    }

    private void processNecessaryPhotos(RequestTemplateForCreatingEventDTO requestTemplateForCreatingEvent,
                                        Event newEvent) {

        if (requestTemplateForCreatingEvent.getAvatarsForAdding() != null) {
            List<String> savedAvatarsInStorage = photoStorageService
                    .addEventAvatars(requestTemplateForCreatingEvent.getAvatarsForAdding());
            List<EventAvatar> savedAvatars = eventAvatarService.saveEventAvatars(savedAvatarsInStorage, newEvent);
            newEvent.addEventAvatars(new HashSet<>(savedAvatars));
            eventService.save(newEvent);
        }
        if (requestTemplateForCreatingEvent.getAvatarsForDeleting() != null) {
            List<String> avatarsForDeleting = new ArrayList<>();
            avatarsForDeleting.addAll(requestTemplateForCreatingEvent.getAvatarsForDeleting());
            avatarsForDeleting.addAll(requestTemplateForCreatingEvent.getAvatarsForAdding());
            photoStorageService.deletePhotosInTmpDir(avatarsForDeleting);
        }
    }
}
