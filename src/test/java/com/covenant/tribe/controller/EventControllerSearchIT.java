package com.covenant.tribe.controller;

import com.covenant.tribe.dto.auth.TokensDTO;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.service.facade.EventSearchFacade;
import com.covenant.tribe.service.impl.ElasticContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@SpringBootTest
@DirtiesContext
public class EventControllerSearchIT extends ElasticContainer {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private EventSearchFacade eventSearchFacade;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    @BeforeEach
    void init() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
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

    @Test
    public void events_found() throws Exception {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("text", "INSTASAMKA");
        MockHttpServletRequestBuilder string = get("/api/v1/events/search")
                .params(map)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));
        ResultActions perform = this.mockMvc.perform(string);
        List<SearchEventDTO> list = getOnbjs(perform);
        assertThat(list.get(0).getEventId(), is(equalTo(1003L)));
    }

    @Test
    public void events_found_by_text_and_filter() throws Exception {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("text", "КУОК");
        map.add("isFree", "true");
        MockHttpServletRequestBuilder string = get("/api/v1/events/search")
                .params(map)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));
        ResultActions perform = this.mockMvc.perform(string);
        List<SearchEventDTO> list = getOnbjs(perform);
        assertThat(list.get(0).getEventId(), is(equalTo(1001L)));
    }

    /**
     * Checks if search sorts by relevance
     */
    @Test
    public void events_found_by_relevance() throws Exception {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("text", "КУОК");
        MockHttpServletRequestBuilder string = get("/api/v1/events/search")
                .params(map)
                .header(HttpHeaders.AUTHORIZATION, "Bearer "
                        + obtainAccessToken("test1@gmail.com", "string"));
        ResultActions perform = this.mockMvc.perform(string);
        List<SearchEventDTO> list = getOnbjs(perform);
        assertThat(list.get(0).getEventId(), is(equalTo(1000L)));
        assertThat(list.get(1).getEventId(), is(equalTo(1002L)));
        assertThat(list.get(2).getEventId(), is(equalTo(1001L)));
    }

    private List<SearchEventDTO> getOnbjs(ResultActions perform) throws JsonProcessingException, UnsupportedEncodingException {
        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(contentAsString).get("content");
        ArrayList<SearchEventDTO> list = objectMapper.readValue(jsonNode.toString(),
                objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, SearchEventDTO.class));
        return list;
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        AuthRequestDto preparedRequest = new AuthRequestDto(email, password);

        var requestBuilder = post("/api/v1/auth/login/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preparedRequest));

        MvcResult result = this.mockMvc.perform(requestBuilder).andReturn();

        TokensDTO token = objectMapper.readValue(result.getResponse().getContentAsString(), TokensDTO.class);
        return token.getAccessToken();
    }
}
