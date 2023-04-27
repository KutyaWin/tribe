package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;

public interface UserMapper {

    User mapToUser(UserForSignInUpDTO userForSignInUpDTO, String socialTypeFromHeader);

    UserForSignInUpDTO mapToTESTUserForSignUpDTO(User user);

    UserToSendInvitationDTO mapToUserToSendInvitationDTO(User user);
}
