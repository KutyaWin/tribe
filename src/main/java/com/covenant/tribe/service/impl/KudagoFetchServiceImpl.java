package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.EventRequestQueryMap;
import com.covenant.tribe.client.kudago.KudagoClient;
import com.covenant.tribe.client.kudago.dto.KudagoClientParams;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.client.kudago.dto.KudagoEventsResponseDto;
import com.covenant.tribe.service.KudagoFetchService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoFetchServiceImpl implements KudagoFetchService {

    @Autowired
    KudagoClient kudaGoClient;
    String fields;
    String expand;
    @JsonProperty(value = "text_format")
    String textFormat;
    @JsonProperty(value = "order_by")
    String orderBy;

    @PostConstruct
    public void init() {
        List<String> fieldsToRetrieve = List.of("id", "publication_date", "place", "location", "dates", "title", "slug", "age_restriction", "price", "body_text", "categories", "images", "tags");
        List<String> detailedFields = List.of("place", "dates", "location", "categories", "images");
        textFormat = "text";
        orderBy = "-publication_date";

        fields = String.join(",", fieldsToRetrieve);
        expand = String.join(",", detailedFields);
    }

    @Override
    public Map<Long, KudagoEventDto> fetchPosts(Long sincePublicationDate) throws JsonProcessingException {
        List<KudagoEventDto> events = new ArrayList<>();
        Integer page = 0;
        Integer pageSize = 100;
        List<KudagoEventDto> currentPage;
        while (true) {
            log.info("Current page is {}", page);
            page++;
            currentPage = fetchPosts(page, pageSize);
            if (currentPage == null || currentPage.isEmpty()) {
                break;
            } else {
                events.addAll(currentPage);
                if (checkPublicationDate(currentPage, sincePublicationDate)) {
                    break;
                }
                ;
                log.info("Current page has {} events", events.size());
            }
        }
        Map<Long, KudagoEventDto> map = events.stream().collect(Collectors.toMap(KudagoEventDto::getId, e -> e));
        return map;
    }

    private boolean checkPublicationDate(List<KudagoEventDto> currentPage, Long sincePublicationDate) {
        KudagoEventDto lastEventInPage = currentPage.get(currentPage.size() - 1);
        Long publicationDate = lastEventInPage.getPublicationDate();
        return publicationDate < sincePublicationDate;
    }

    @Override
    public List<KudagoEventDto> fetchPosts(Integer page, Integer pageSize) throws JsonProcessingException {
        log.debug("request for page {}", page);
        EventRequestQueryMap.EventRequestQueryMapBuilder builder = EventRequestQueryMap.builder()
                .fields(fields)
                .expand(expand)
                .text_format(textFormat)
                .order_by(orderBy)
                .page(page)
                .page_size(pageSize);
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
