package com.covenant.tribe.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.util.ObjectBuilder;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventIdView;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.covenant.tribe.domain.event.search.EventSearchUnitFactory;
import com.covenant.tribe.exeption.event.search.EventSearchUnitNotFoundException;
import com.covenant.tribe.repository.EventSearchUnitRepository;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.impl.pojo.SearchFunctionParam;
import com.covenant.tribe.util.reflection.UpdateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ConditionalOnProperty(value = "elastic.enabled", havingValue = "true")
public class EventSearchServiceImpl implements EventSearchService {

    EventSearchUnitFactory eventSearchUnitFactory;

    EventSearchUnitRepository eventSearchUnitRepository;

    ElasticsearchTemplate client;

    List<SearchFunctionParam> searchFunctionParams = new ArrayList<>();
    @PostConstruct
    public void addSearchFields() {
        searchFunctionParams.add(new SearchFunctionParam("eventName", 2.0F));
        searchFunctionParams.add(new SearchFunctionParam("eventAddress.names", 1.5F));
        searchFunctionParams.add(new SearchFunctionParam("eventDescription", 1.0F));
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
    public List<EventSearchUnit> findByTextAndIds(String text, Pageable pageable) throws JsonProcessingException {
        return findByTextAndIds(text, pageable, new ArrayList<>());
    }

    @Override
    @Transactional
    public List<EventSearchUnit> findByTextAndIds(String text, Pageable pageable, List<EventIdView> ids) throws JsonProcessingException {
        var tokens = getTokens(text);
        NativeQueryBuilder query = NativeQuery.builder().withQuery(q -> q.bool(allTokens -> {
            for (String token : tokens) {
                allTokens.must(perTokenQ -> perTokenQ.bool(b -> {
                    b.minimumShouldMatch(String.valueOf(2));
                    for (SearchFunctionParam param : searchFunctionParams) {
                        b.should(m -> m.functionScore(getFunctionScore(token, param)));
                    }
                    b.should(m -> m.matchAll(getMatchAll()));
                    return b;
                }));
            }
            return allTokens;
        })).withPageable(pageable);
        if (!ids.isEmpty()) {
            List<Long> list = ids.stream().map(i -> i.getId()).toList();
            List<String> list1 = list.stream().map(l -> String.valueOf(l)).toList();
            TermsSetQuery.Builder id = QueryBuilders.termsSet().field("id").terms(list1).minimumShouldMatchScript(s -> s.inline(i -> i.source(String.valueOf(1))));
            query.withFilter(f->f.termsSet(id.build()));
        }
        NativeQuery build = query.build();
        SearchHits<EventSearchUnit> hits = client.search(build, EventSearchUnit.class, IndexCoordinates.of("event"));
        return hits.get().map(SearchHit::getContent).collect(Collectors.toList());

    }

    private static Function<FunctionScoreQuery.Builder, ObjectBuilder<FunctionScoreQuery>> getFunctionScore(String val,SearchFunctionParam param) {
        return fs -> fs.query(
                    fq -> fq.fuzzy(
                            f -> f.field(param.getField()).value(val)
                    )
        ).scoreMode(FunctionScoreMode.Sum).boost(param.getBoost());
    }

    private MatchAllQuery getMatchAll() {
        MatchAllQuery.Builder builder = QueryBuilders.matchAll().boost(0.0F);
        return builder.build();
    }

    @Override
    public void deleteAll() {
        eventSearchUnitRepository.deleteAll();
    }

    private List<String> getTokens(String text) {
        return Arrays.stream(text.trim().split("[\\s-,|]+")).map(String::toLowerCase).toList();
    }

    private FunctionScoreQuery.Builder getQueryBuilder(String token, String field, Float boost) {
        return QueryBuilders
                .functionScore().query(QueryBuilders.fuzzy().field(field).value(token).build()._toQuery())
                .scoreMode(FunctionScoreMode.Sum)
                .boost(boost);
    }
}
