package com.covenant.tribe.domain.event.search;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRelationsWithUserSearchUnit {
   
    Long id;
    
    Long userId;
    
    boolean isInvited;

    boolean isParticipant;

    boolean isWantToGo;

    boolean isFavorite;

    boolean isViewed;
}