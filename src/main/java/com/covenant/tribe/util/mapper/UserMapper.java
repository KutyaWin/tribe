package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.TESTUserForSignUpDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;

public interface UserMapper {

    User mapToUser(TESTUserForSignUpDTO testUserForSignUpDTO);

    TESTUserForSignUpDTO mapToTESTUserForSignUpDTO(User user);

    UserToSendInvitationDTO mapToUserToSendInvitationDTO(User user);
}
