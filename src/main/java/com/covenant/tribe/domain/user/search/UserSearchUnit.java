package com.covenant.tribe.domain.user.search;

import com.covenant.tribe.domain.event.search.EventRelationsWithUserSearchUnit;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSearchUnit {
    
    Long id;

    List<EventRelationsWithUserSearchUnit> relations;
}