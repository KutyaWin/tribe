package com.covenant.tribe.service.impl.EventSearchService;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventIdView;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.covenant.tribe.domain.event.search.EventSearchUnitFactory;
import com.covenant.tribe.exeption.event.search.EventSearchUnitNotFoundException;
import com.covenant.tribe.repository.EventSearchUnitRepository;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchFields;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchFunctionParam;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchFunctionTerm;
import com.covenant.tribe.service.impl.EventSearchService.pojo.SearchParams;
import com.covenant.tribe.service.impl.EventSearchService.processors.SearchProcessor;
import com.covenant.tribe.util.reflection.UpdateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@ConditionalOnProperty(value = "elastic.enabled", havingValue = "true")
public class EventSearchServiceImpl implements EventSearchService {

    EventSearchUnitFactory eventSearchUnitFactory;

    EventSearchUnitRepository eventSearchUnitRepository;

    ElasticsearchTemplate client;

    List<SearchProcessor> searchProcessors;

    List<SearchFunctionParam> searchFunctionParams = new ArrayList<>();

    List<SearchFunctionTerm> searchFunctionTerms = new ArrayList<>();
    @PostConstruct
    public void addSearchFields() {
        searchFunctionParams.add(new SearchFunctionParam("eventName", 2.0F, 2));
        searchFunctionParams.add(new SearchFunctionParam("eventAddress.names", 1.5F, 2));
        searchFunctionParams.add(new SearchFunctionParam("eventDescription", 1.0F, 2));

        searchFunctionTerms.add(new SearchFunctionTerm(SearchFields.IDS.getName(), false));
        searchFunctionTerms.add(new SearchFunctionTerm(SearchFields.TAGLIST.getName(), true));
    }

    @Override
    @Transactional
    public EventSearchUnit create(Event event) {
        EventSearchUnit eventSearchUnit = eventSearchUnitFactory.create(event);
        return eventSearchUnitRepository.save(eventSearchUnit);
    }

    @Override
    @Transactional
    public EventSearchUnit update(Event event) {
        EventSearchUnit oldUnit = getSafe(event);
        EventSearchUnit newUnit = eventSearchUnitFactory.create(event);
        UpdateUtil.updateEntity(oldUnit, newUnit);
        UpdateUtil.updateEntity(newUnit.getEventAddress(), newUnit.getEventAddress());
        return eventSearchUnitRepository.save(oldUnit);
    }

    private EventSearchUnit getSafe(Event event) {
        return eventSearchUnitRepository.findById(String.valueOf(event.getId())).orElseThrow(() -> new EventSearchUnitNotFoundException("Event search unit not found"));
    }

    @Override
    @Transactional
    public List<EventSearchUnit> findByText(String text, Pageable pageable) throws JsonProcessingException {
        return findByText(text, pageable, new ArrayList<>());
    }

    @Override
    @Transactional
    public List<EventSearchUnit> findByText(String text, Pageable pageable, List<EventIdView> ids) throws JsonProcessingException {
        NativeQuery build = getRequest(text, ids, pageable);
        SearchHits<EventSearchUnit> hits = getResponse(build);
        return hits.get().map(SearchHit::getContent).collect(Collectors.toList());

    }

    @NotNull
    private NativeQuery getRequest(String text, List<EventIdView> ids, Pageable pageable) {
        Map<String, List<String>> terms = new HashMap<>();
        List<String> idsS = ids.stream().map(i -> String.valueOf(i.getId())).toList();
        terms.put(SearchFields.IDS.getName(), idsS);

        BoolQuery.Builder bool = QueryBuilders.bool();
        BoolQuery.Builder filter = QueryBuilders.bool();
        SearchParams searchParams = SearchParams.builder()
                .fieldParams(searchFunctionParams)
                .terms(searchFunctionTerms)
                .text(text)
                .field2termList(terms)
                .rootBool(bool)
                .rootFilter(filter)
                .pageable(pageable)
                .build();
        SearchProcessor searchChain = getProcessorsChain();
        searchChain.process(searchParams);
        NativeQueryBuilder builder = NativeQuery.builder();
        NativeQuery build = getQuery(pageable, searchParams, builder);
        return build;
    }

    @NotNull
    private static NativeQuery getQuery(Pageable pageable, SearchParams searchParams, NativeQueryBuilder builder) {
        return builder
                .withQuery(
                        q -> q.bool(
                                searchParams.getRootBool()
                                        .minimumShouldMatch(String.valueOf(1)).build())
                )
                .withFilter(
                        q -> q.bool(
                                searchParams.getRootFilter().build())
                )
                .withPageable(pageable).build();
    }

    @NotNull
    private SearchHits<EventSearchUnit> getResponse(NativeQuery build) {
        SearchHits<EventSearchUnit> hits = client.search(build, EventSearchUnit.class, IndexCoordinates.of("event"));
        log.debug(String.format("found: %s",String.join(", ", extracted(hits))));
        return hits;
    }

    private List<String> extracted(SearchHits<EventSearchUnit> hits) {
        return hits.stream().map(h -> h.getContent().toString()).toList();
    }

    private SearchProcessor getProcessorsChain() {
        List<SearchProcessor> reversed = searchProcessors.stream().sorted(Comparator.comparingInt(SearchProcessor::getOrder).reversed()).toList();
        SearchProcessor searchChain = reversed.stream().reduce((p2, p1) -> {
            p1.setNext(p2);
            return p1;
        }).orElseThrow(() -> new RuntimeException("No SearchProcessors"));
        return searchChain;
    }

    @Override
    public void deleteAll() {
        eventSearchUnitRepository.deleteAll();
    }

}
