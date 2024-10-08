package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.auth.AuthMethodsDto;
import com.covenant.tribe.dto.auth.ConfirmRegistrationDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.covenant.tribe.dto.user.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserMapper {

    User mapToUserFromUserGoogleRegistration(
            UserForSignInUpDTO userForSignInUpDTO,
            String googleUserId
    );

    User mapToUserFromUserVkRegistration(UserForSignInUpDTO userForSignInUpDTO, String vkUserId);

    ProfileDto mapToProfileDto(
            User user, boolean isFollowed, boolean isFollowing, Long chatId
    );

    UserToSendInvitationDTO mapToUserToSendInvitationDTO(User user);

    User buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(
            ConfirmRegistrationDTO confirmRegistrationDTO, Set<EventType> userInterests, Registrant registrant
    );

    UserSubscriberDto mapToUserSubscriberDto(User follower, Set<Long> userIds, Map<Long, Long> userIdsWithChatsExist);

    UserUnSubscriberDto mapToUserUnSubscriberDto(User user);

    UserGetDto mapToUserGetDto(
            User user, AuthMethodsDto authMethodsDto, List<ProfessionDto> professionDto,
            List<EventTypeInfoDto> eventTypeInfoDtoList
    );

}
