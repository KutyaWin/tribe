package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.auth.AuthMethodsDto;
import com.covenant.tribe.dto.auth.ConfirmRegistrationDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.covenant.tribe.dto.user.*;

import java.util.List;
import java.util.Set;

public interface UserMapper {

    User mapToUserFromUserForSignInUpDTO(UserForSignInUpDTO userForSignInUpDTO, String socialTypeFromHeader);

    UserForSignInUpDTO mapToTESTUserForSignUpDTO(User user);

    UserToSendInvitationDTO mapToUserToSendInvitationDTO(User user);

    User buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(
            ConfirmRegistrationDTO confirmRegistrationDTO, Set<EventType> userInterests, Registrant registrant
    );

    UserSubscriberDto mapToUserSubscriberDto(User follower, Set<Long> userIds);

    UserUnSubscriberDto mapToUserUnSubscriberDto(User user);

    UserProfileGetDto mapToUserProfileGetDto(
            User user, AuthMethodsDto authMethodsDto, List<ProfessionDto> professionDto,
            List<EventTypeInfoDto> eventTypeInfoDtoList
    );
}
