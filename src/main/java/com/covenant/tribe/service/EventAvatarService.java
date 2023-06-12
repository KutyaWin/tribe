package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAvatar;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventAvatarService {

    List<EventAvatar> saveEventAvatars(List<String> eventAvatarNames, Event event);

}
