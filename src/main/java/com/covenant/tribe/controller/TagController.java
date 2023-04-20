package com.covenant.tribe.controller;

import com.covenant.tribe.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("api/v1/tags")
public class TagController {

    TagService tagService;

    @Operation(
            tags = "TagController",
            description = "Find a tag that contains a name")
    @GetMapping("{tagName}")
    public ResponseEntity<?> getTagsByNameContains(@PathVariable("tagName") String tagName) {
        log.info("[CONTROLLER] start endpoint getTagsByNameContains with param: {}", tagName);

        List<String> response = tagService.getTagsByContainingName(tagName);

        log.info("[CONTROLLER] end endpoint getTagsByNameContains with response: {}", response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
