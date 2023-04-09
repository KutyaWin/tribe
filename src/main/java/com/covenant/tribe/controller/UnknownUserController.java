package com.covenant.tribe.controller;

import com.covenant.tribe.dto.user.UnknownUserWithInterestsDTO;
import com.covenant.tribe.service.UnknownUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/unknown-user")
public class UnknownUserController {

    UnknownUserService unknownUserService;

    @PostMapping("/interests")
    public ResponseEntity<?> saveUnknownUserWithInterests(
            @RequestBody UnknownUserWithInterestsDTO unknownUserWithInterests
    ) {
        Long unknownUserId = unknownUserService.saveNewUnknownUserWithInterests(
                unknownUserWithInterests
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(unknownUserId);
    }

}
