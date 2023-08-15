package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventIdView;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.facade.EventSearchFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@SpringBootTest
public class EventSearchServiceIT extends ElasticContainer {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EventSearchFacade eventSearchFacade;

    @Autowired
    private EventSearchService eventSearchService;

    @PostConstruct
    public void beforeAll() throws IOException, SQLException {
        ClassPathResource deleteAll = new ClassPathResource("sql/events/search/delete_data_for_event_search.sql");
        ClassPathResource createAll = new ClassPathResource("sql/events/search/init_data_for_event_search.sql");
        try(Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, deleteAll);
            ScriptUtils.executeSqlScript(connection, createAll);
        }
        eventSearchFacade.updateAll();
    }

    @PreDestroy
    public void afterAll() throws IOException, SQLException {
        ClassPathResource resource = new ClassPathResource("sql/events/search/delete_data_for_event_search.sql");
        try(Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, resource);
        }
    }
    @Test
    public void if_context_loads_then_no_exception() {}

    /**
     * Checks that entity found
     */
    @Test
    public void event_found() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("INSTASAMKA", of);
        assertThat(byTextAndIds.get(0).getId(), is(equalTo(1003L)));
    }

    /**
     * Checks that results ranked by best fields
     */
    @Test
    public void event_found_and_ranked_by_number_of_words_matching() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("INSTASAMKA MONEYKEN", of);
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(list.get(0), is(equalTo(1001L)));
        assertThat(list, hasItems(1002L, 1003L));
    }

    /**
     * Checks if entities found by fuzzy query
     */
    @Test
    public void found_by_name_fuzzy() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("ENSTOSAMKA ", of);
        assertThat(byTextAndIds.get(0).getId(), is(equalTo(1003L)));
    }

    /**
     * Checks that entities with more relevant name are higher
     */
    @Test
    public void found_most_relevant_and_others() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("INSTASAMKA", of);
        assertThat(byTextAndIds.get(0).getId(), is(equalTo(1003L)));
        assertThat(byTextAndIds.get(1).getId(), is(equalTo(1001L)));
    }

    /**
     * Checks if entities found by city
     */
    @Test
    public void found_by_name_and_city() throws JsonProcessingException {
        //given
        PageRequest of = PageRequest.of(0, 100);
        String text = "INSTASAMKA Воронеж";
        //when
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText(text, of);
        //then
        assertThat(byTextAndIds.get(0).getId(), is(equalTo(1001L)));
    }

    /**
     * Check if search returns only entities with specified ids
     */
    @Test
    public void found_by_ids_in() throws JsonProcessingException {
        //given
        PageRequest of = PageRequest.of(0, 100);
        List<EventIdView> eventIdViews = getEventIdViews();
        //when
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("INSTASAMKA", of, eventIdViews);
        //then
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(1003L, is(not(in(list))));
    }

    /**
     * Check if search returns only entities with specified ids
     */
    @Test
    public void found_by_ids_in_with_result() throws JsonProcessingException {
        //given
        PageRequest of = PageRequest.of(0, 100);
        //when
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("INSTASAMKA", of);
        //then
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(1003L, is(in(list)));
    }

    /**
     * Check if search returns results by tags
     */
    @Test
    public void found_by_tags_in_with_result() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventIdView> eventIdViews =  getEventIdViewsAll();
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("пивица", of, eventIdViews);
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(list, hasItems(1001L, 1002L));
    }

    /**
     * Check if search returns results by tags with relevance
     */
    @Test
    public void found_by_tags_with_more_tags_more_relevance() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventIdView> eventIdViews =  getEventIdViewsAll();
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("пивица пивец", of, eventIdViews);
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(list.get(0), equalTo(1002L));
    }

    /**
     * Check if search returns results with tags and name
     */
    @Test
    public void found_by_tags_with_name() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("пивица instasamka", of);
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(list.get(0), equalTo(1001L));
    }


    /**
     * Check if search returns results containing only one word
     */
    @Test
    public void found_all_containing_both_words() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("Green Plaza", of);
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(list, hasItems(1001L, 1002L));
    }

    /**
     * Check if search ranks results with two words higher than with only one
     */
    @Test
    public void found_all_containing_both_words_and_ranking_correct() throws JsonProcessingException {
        PageRequest of = PageRequest.of(0, 100);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByText("MONEYKEN КУОК", of);
        List<Long> list = byTextAndIds.stream().map(EventSearchUnit::getId).toList();
        assertThat(list.get(0), equalTo(1001L));
        assertThat(list, hasItems(1000L, 1003L));
    }

    @NotNull
    private List<EventIdView> getEventIdViews() {
        return Stream.of(new Long[]{1000L, 1001L, 1002L})
                .map(EventIdViewImpl::new)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<EventIdView> getEventIdViewsAll() {
        return Stream.of(new Long[]{1000L, 1001L, 1002L, 1003L})
                .map(EventIdViewImpl::new)
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    @Getter
    @Setter
    class EventIdViewImpl implements EventIdView {
        private Long id;
    }
}
