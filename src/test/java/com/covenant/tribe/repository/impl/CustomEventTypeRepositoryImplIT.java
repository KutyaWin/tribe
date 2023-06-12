package com.covenant.tribe.repository.impl;

import com.covenant.tribe.AbstractTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

//Integration test
@Sql("/sql/init_fetch_event_type.sql")
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomEventTypeRepositoryImplIT extends AbstractTestcontainers {

    @Autowired
    CustomEventTypeRepositoryImpl customEventTypeRepository;

    @Test
    void findEventTypeByIdFetchEventListWithTypeAndTagList_itShouldFindEventTypeByIdWithFetchedFields() {
        //given
        Long expectedEventTypeId = 1000L;

        //when
        var eventType = customEventTypeRepository
                .findEventTypeByIdFetchEventListWithTypeAndTagList(expectedEventTypeId);

        //then
        assertThat(eventType.isPresent()).isTrue();
        assertThat(eventType.get().getId()).isEqualTo(expectedEventTypeId);
        assertThat(eventType.get().getEventListWithType()).isNotEmpty();
        assertThat(eventType.get().getTagList()).isNotEmpty();
    }

    @Test
    void findEventTypeByIdFetchEventListWithTypeAndTagList_itShouldDoesNotFindEventTypeByIdWithFetchedFields() {
        //given
        Long expectedEventTypeId = 1003L;

        //when
        var eventType = customEventTypeRepository
                .findEventTypeByIdFetchEventListWithTypeAndTagList(expectedEventTypeId);

        //then
        assertThat(eventType.isPresent()).isFalse();
    }
}