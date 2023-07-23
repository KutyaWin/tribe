package com.covenant.tribe.controller;

import com.covenant.tribe.service.EventSearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/replica")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReplicatorController {

    EventSearchService eventSearchService;

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/create_events")
    @ResponseStatus(HttpStatus.OK)
    public void updateEventsSearchUnits() {
        eventSearchService.updateAll();
    }
}
