package com.covenant.tribe.domain.event.search;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.search.UserSearchUnit;
import com.covenant.tribe.domain.user.search.UserSearchUnitFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventSearchUnitFactory {

    UserSearchUnitFactory userSearchUnitFactory;
    EventAddressSearchUnitFactory addressSearchUnitFactory;

    public EventSearchUnit create(Event event) {
        List<UserRelationsWithEvent> eventRelationsWithUser = event.getEventRelationsWithUser();
        List<UserSearchUnit> userSearchUnits = getUserSearchUnitList(eventRelationsWithUser);
        EventAddressSearchUnit addressSearchUnit = addressSearchUnitFactory.create(event);
        return EventSearchUnit.builder()
                .id(event.getId())
                .eventAddress(addressSearchUnit)
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .eventType(event.getEventType().getTypeName())
                .taglist(event.getTagList().stream().map(Tag::getTagName).toList())
                .users(userSearchUnits)
                .build();
    }

    private List<UserSearchUnit> getUserSearchUnitList(List<UserRelationsWithEvent> eventRelationsWithUser) {
        return eventRelationsWithUser.stream().map(UserRelationsWithEvent::getUserRelations).toList().stream().map(userSearchUnitFactory::create).toList();
    }
}