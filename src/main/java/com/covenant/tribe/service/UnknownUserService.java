package com.covenant.tribe.service;

import com.covenant.tribe.dto.user.UnknownUserWithInterestsDTO;
import org.springframework.stereotype.Service;

@Service
public interface UnknownUserService {
    Long saveNewUnknownUserWithInterests(UnknownUserWithInterestsDTO unknownUserWithInterests);
}
