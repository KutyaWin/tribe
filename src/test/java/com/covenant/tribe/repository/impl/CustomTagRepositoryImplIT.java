package com.covenant.tribe.repository.impl;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.domain.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Sql("/sql/init_fetch_tags.sql")
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomTagRepositoryImplIT extends AbstractTestcontainers {

    @Autowired
    CustomTagRepositoryImpl customTagRepository;

    @Test
    void findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong_itShouldFindAllTagsWithFetchedFields() {
        //given
        Set<Long> IdsToFind = Set.of(1000L, 1001L, 1002L);

        //when
        List<Tag> actual = customTagRepository
                .findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(IdsToFind);

        //then
        assertThat(actual).isNotEmpty().hasSize(3);
        assertThat(actual.stream().map(Tag::getId).toList())
                .containsAll(new ArrayList<>(IdsToFind));
        assertThat(actual.stream().map(Tag::getEventListWithTag).toList()).isNotEmpty();
        assertThat(actual.stream().map(Tag::getEventTypesToWhichTagBelong).toList()).isNotEmpty();
    }

    @Test
    void findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong_itShouldDoesNotFindOneTag() {
        //given
        Set<Long> idsToFind = Set.of(1002L, 1003L);

        //when
        List<Tag> actual = customTagRepository
                .findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(idsToFind);

        //then
        assertThat(actual).hasSize(1);
        assertThat(actual.stream().map(Tag::getId).toList()).containsOnly(1002L);
        assertThat(actual.stream().map(Tag::getEventListWithTag).toList()).isNotEmpty();
        assertThat(actual.stream().map(Tag::getEventTypesToWhichTagBelong).toList()).isNotEmpty();
    }

    @Test
    void findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong_itShouldDoesNotFindAnyTags() {
        //given
        Set<Long> idsToFind = Set.of(1003L);

        //when
        List<Tag> actual = customTagRepository
                .findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(idsToFind);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual).isEmpty();
    }
}