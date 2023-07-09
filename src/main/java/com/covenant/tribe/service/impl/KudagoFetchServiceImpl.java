package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.EventRequestQueryMap;
import com.covenant.tribe.client.kudago.KudagoClient;
import com.covenant.tribe.client.kudago.dto.KudagoClientParams;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.client.kudago.dto.KudagoEventsResponseDto;
import com.covenant.tribe.service.KudagoFetchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoFetchServiceImpl implements KudagoFetchService {
    final KudagoClient kudaGoClient;
    String fields;
    String expand;

    @PostConstruct
    public void init() {
        List<String> fieldsToRetrieve = List.of("id","publication_date", "place", "location", "dates", "title", "slug", "age_restriction", "price", "body_text", "categories", "images");
        List<String> detailedFields = List.of("place", "dates", "location", "categories", "images");

        fields = String.join(",", fieldsToRetrieve);
        expand = String.join(",", detailedFields);
    }

    @Override
    public List<KudagoEventDto> fetchPosts(KudagoClientParams kudagoClientParams) throws JsonProcessingException {
        List<KudagoEventDto> events = new ArrayList<>();
        Integer page = 0;
        Integer pageSize = 100;
        List<KudagoEventDto> currentPage;
        while (true) {
            page++;
            currentPage = fetchPosts(page, pageSize, kudagoClientParams);
            if (currentPage == null || currentPage.isEmpty()){
                break;
            } else {
                events.addAll(currentPage);
            }
        }
        return events;
    }

    @Override
    public List<KudagoEventDto> fetchPosts(Integer page, Integer pageSize, KudagoClientParams kudagoClientParams) throws JsonProcessingException {
        log.debug("request for page {}", page);
        EventRequestQueryMap.EventRequestQueryMapBuilder builder = EventRequestQueryMap.builder()
                .fields(fields)
                .expand(expand)
                .page(page)
                .page_size(pageSize);
        if (kudagoClientParams.getActual_since()!=null) {
            builder.actual_since(kudagoClientParams.getActual_since());
        }
        EventRequestQueryMap request = builder.build();
        ResponseEntity<KudagoEventsResponseDto> events;
        try {
            events = kudaGoClient.getEvents(request);
            return events.getBody().getResults();
        } catch (FeignException.NotFound e) {
          log.info("Page {} not found", page);
          return null;
        }
    }
}
