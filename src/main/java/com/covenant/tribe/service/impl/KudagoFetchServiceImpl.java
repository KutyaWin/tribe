package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.EventRequestQueryMap;
import com.covenant.tribe.client.kudago.KudagoClient;
import com.covenant.tribe.client.kudago.dto.KudagoEventsResponseDto;
import com.covenant.tribe.service.KudagoFetchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoFetchServiceImpl implements KudagoFetchService {
    final KudagoClient kudaGoClient;

    List<String> fieldsToRetrieve;

    List<String> detailedFields;

    @PostConstruct
    public void init() {
        fieldsToRetrieve = List.of(new String[]{
                "id",
                "place",
                "location",
                "dates",
                "title",
                "slug",
                "age_restriction",
                "price"});
        detailedFields = List.of(new String[]{
                "place",
                "dates"});
    }

    @Override
    public KudagoEventsResponseDto fetchPosts() throws JsonProcessingException {
        String fields = String.join(",", fieldsToRetrieve);
        EventRequestQueryMap build = EventRequestQueryMap.builder().
                fields(fields)
                .page(1)
                .page_size(100)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        ResponseEntity<KudagoEventsResponseDto> events = kudaGoClient.getEvents(build);
        return events.getBody();
    }
}
