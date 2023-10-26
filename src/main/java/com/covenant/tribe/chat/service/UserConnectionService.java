package com.covenant.tribe.chat.service;

import java.security.Principal;

public interface UserConnectionService {
    void userConnected(Principal userId);
    void userDisconnected(Principal userId);

}
