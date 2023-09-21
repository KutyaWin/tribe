package com.covenant.tribe.dto.event;

import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDto implements Serializable {

    @JsonProperty("event_type_info_dto")
    EventTypeInfoDto eventTypeInfoDto;

    @JsonProperty("event_contacts")
    Set<EventContactInfoDto> eventContactInfos;

    @JsonProperty("avatar_urls")
    List<String> avatarUrls;

    String name;

    @JsonProperty("address_dto")
    EventAddressDTO addressDTO;

    @JsonProperty("event_start_date_time")
    LocalDateTime startDateTime;

    @JsonProperty("event_end_date_time")
    LocalDateTime endDateTime;

    @JsonProperty("event_tags")
    List<EventTagDTO> tags;

    String description;

    List<UserToSendInvitationDTO> invitations;

    String timezone;

    @JsonProperty("is_private")
    boolean isPrivate;

    @JsonProperty("is_show_in_search")
    boolean isShowInSearch;

    @JsonProperty("is_send_by_interests")
    boolean isSendByInterests;

    @JsonProperty("is_eighteen_year_limit")
    boolean isEighteenYearLimit;

    @JsonProperty("has_alcohol")
    boolean hasAlcohol;

    @JsonProperty("is_free")
    boolean isFree;

}
