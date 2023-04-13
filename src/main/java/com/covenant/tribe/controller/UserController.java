package com.covenant.tribe.controller;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.user.UserDTO;
import com.covenant.tribe.dto.user.UserFavoriteEventDTO;
import com.covenant.tribe.mapper.UserMapper;
import com.covenant.tribe.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    UserService userService;

    @PostMapping
    public ResponseEntity<?> saveNewUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("[CONTROLLER] start endpoint saveNewUser with param: {}", userDTO);
        User savedUser = userService.saveUser(userDTO);
        log.debug("[CONTROLLER] end endpoint saveNewUser with response: {}", savedUser);
        return new ResponseEntity<>(UserMapper.mapUserToUserDTO(savedUser), HttpStatus.OK);
    }

    @PostMapping("/favorite")
    public ResponseEntity<?> saveEventToFavorites(@RequestBody UserFavoriteEventDTO userFavoriteEventDTO) {
        userService.saveEventToFavorite(userFavoriteEventDTO.getUserId(), userFavoriteEventDTO.getEventId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

}
