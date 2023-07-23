package com.covenant.tribe.domain.user.search;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.search.EventRelationsWithUserSearchUnit;
import com.covenant.tribe.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSearchUnitFactory {

    public UserSearchUnit create(User user) {
        List<UserRelationsWithEvent> userRelationsWithEvents = user.getUserRelationsWithEvents();
        return UserSearchUnit.builder()
                .id(user.getId())
                .relations(userRelationsWithEvents.stream().map(this::createRel).toList())
                .build();
    }

    public EventRelationsWithUserSearchUnit createRel(UserRelationsWithEvent relationsWithEvent) {
        return EventRelationsWithUserSearchUnit.builder()
                .id(relationsWithEvent.getId())
                .userId(relationsWithEvent.getUserRelations().getId())
                .isFavorite(relationsWithEvent.isFavorite())
                .isInvited(relationsWithEvent.isInvited())
                .isParticipant(relationsWithEvent.isParticipant())
                .isWantToGo(relationsWithEvent.isWantToGo())
                .isViewed(relationsWithEvent.isViewed())
                .build();
    }
}