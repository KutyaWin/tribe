package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.auth.ConfirmRegistrationDTO;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;

import java.util.Set;

public interface UserMapper {

    User mapToUserFromUserForSignInUpDTO(UserForSignInUpDTO userForSignInUpDTO, String socialTypeFromHeader);

    UserForSignInUpDTO mapToTESTUserForSignUpDTO(User user);

    UserToSendInvitationDTO mapToUserToSendInvitationDTO(User user);

    User buildUserFromConfirmRegistrationDTORegistrantAndUserInterests(
            ConfirmRegistrationDTO confirmRegistrationDTO, Set<EventType> userInterests, Registrant registrant
    );
}
