package com.covenant.tribe.repository.impl;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.util.querydsl.EventFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Sql(value = "/sql/init_data_for_eventcontroller.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
@ActiveProfiles("test")
@Import(FilterEventRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FilterEventRepositoryImplIT extends AbstractTestcontainers {

    @Autowired
    FilterEventRepositoryImpl filterEventRepository;

    @Test
    void findAllByFilter() {

        EventFilter eventFilter = EventFilter.builder()
                .eventTypeId(List.of(1000L, 1001L, 1002L))
                .build();

        List<Event> events = filterEventRepository.findAllByFilter(eventFilter);

        assertEquals(2, events.size());
    }
}
