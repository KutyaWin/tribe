package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.service.*;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import com.covenant.tribe.util.mapper.EventMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        //The data collected from different services for mapping to the event.
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
            invitedUserByRequest = userService.findAllById(requestTemplateForCreatingEvent.getInvitedUserIds());
        }
        Event newEvent = eventMapper.mapDtoToEvent(requestTemplateForCreatingEvent, organizer, eventType,
                eventAddress, alreadyExistEventTags, createdEventTagsByRequest, invitedUserByRequest);
        newEvent = eventService.saveNewEvent(newEvent);

        //send notification
        if (newEvent.isSendToAllUsersByInterests()) {
            List<User> allUsersIdWhoInterestingEventType = userService
                    .findAllByInterestingEventTypeContaining(newEvent.getEventType().getId()).stream()
                    .filter(u -> !u.getId().equals(requestTemplateForCreatingEvent.getOrganizerId()))
                    .toList();

            firebaseService.sendNotificationsToUsers(allUsersIdWhoInterestingEventType, newEvent.isEighteenYearLimit(),
                    "Some title",
                    "Текст приглашения нужно придумать, id мероприятия лежит в поле data",
                    newEvent.getId());
        }
        if (requestTemplateForCreatingEvent.getInvitedUserIds() != null) {
            List<User> usersWhoInvited = userService
                    .findAllById(requestTemplateForCreatingEvent.getInvitedUserIds()).stream()
                    .filter(u -> !u.getId().equals(requestTemplateForCreatingEvent.getOrganizerId()))
                    .toList();

            firebaseService.sendNotificationsToUsers(usersWhoInvited, newEvent.isEighteenYearLimit(),
                    "Some title",
                    "Текст приглашения нужно придумать, id мероприятия лежит в поле data",
                    newEvent.getId());
        }

        //save and delete photos in storage
        if (requestTemplateForCreatingEvent.getAvatarsForDeleting() != null) {
            photoStorageService.deletePhotosInTmpDir(requestTemplateForCreatingEvent.getAvatarsForDeleting());
        }
        if (requestTemplateForCreatingEvent.getAvatarsForAdding() != null) {
            List<String> savedAvatarsInStorage = photoStorageService
                    .addEventAvatars(requestTemplateForCreatingEvent.getAvatarsForAdding());
            List<EventAvatar> savedAvatars = eventAvatarService.saveEventAvatars(savedAvatarsInStorage, newEvent);
            newEvent.addEventAvatars(new HashSet<>(savedAvatars));
            eventService.save(newEvent);
        }

        DetailedEventInSearchDTO responseEventDto = eventMapper
                .mapToDetailedEventInSearchDTO(eventService.getEventById(newEvent.getId()), newEvent.getOrganizer());
        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return responseEventDto;
    }
}
