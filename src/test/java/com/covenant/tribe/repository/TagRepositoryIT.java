package com.covenant.tribe.repository;

import com.covenant.tribe.AbstractTestcontainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql("/sql/init_tags.sql")
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryIT extends AbstractTestcontainers {

    @Autowired
    private TagRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void existsByTagName_itShouldCheckIfTagByNameRuExist() {
        //given
        var preparedTag = "tag_ru197";

        //when
        boolean expected = underTest.existsByTagName(preparedTag);

        //then
        assertTrue(expected);
    }

    @Test
    void existsByTagName_itShouldCheckIfTagByNameEnExist() {
        //given
        var preparedTag = "tag_en198";

        //when
        boolean expected = underTest.existsByTagName(preparedTag);

        //then
        assertTrue(expected);
    }

    @Test
    void existsByTagName_itShouldCheckIfTagByNameRuDoesNotExists() {
        //given
        var preparedTag = "tag_ru200";

        //when
        boolean expected = underTest.existsByTagName(preparedTag);

        //then
        assertFalse(expected);
    }

    @Test
    void existsByTagName_itShouldCheckIfTagByNameEnDoesNotExists() {
        //given
        var preparedTag = "tag_en200";

        //when
        boolean expected = underTest.existsByTagName(preparedTag);

        //then
        assertFalse(expected);
    }
}