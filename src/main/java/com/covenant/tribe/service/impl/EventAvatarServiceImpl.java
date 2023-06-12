package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.repository.EventAvatarRepository;
import com.covenant.tribe.service.EventAvatarService;
import com.covenant.tribe.util.mapper.EventAvatarMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EventAvatarServiceImpl implements EventAvatarService {

    EventAvatarRepository eventAvatarRepository;
    EventAvatarMapper eventAvatarMapper;

    @Transactional
    @Override
    public List<EventAvatar> saveEventAvatars(List<String> eventAvatarNames, Event event) {
        return eventAvatarRepository.saveAll(eventAvatarNames.stream()
                .map(avatar -> eventAvatarMapper.mapToEventAvatar(avatar, event))
                .collect(Collectors.toSet()));
    }
}
