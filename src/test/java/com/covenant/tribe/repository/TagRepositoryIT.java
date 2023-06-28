package com.covenant.tribe.repository;

import com.covenant.tribe.AbstractTestcontainers;
import com.covenant.tribe.domain.Tag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void findByTagName() {

        var tagName = "tag_ru1";

        Tag expected = underTest.findByTagName(tagName);

        assertNotNull(expected);
        assertEquals(expected.getTagName(), "tag_ru1");
        assertEquals(expected.getTagNameEn(), "tag_en2");

    }

    @Test
    void findByIdIn(){

        var ids = List.of(1L, 2L, 3L);

        List<Tag> tags = underTest.findByIdIn(ids);

        assertNotNull(tags);
        assertEquals(tags.size(), 3);
        assertEquals(ids.get(0), tags.get(0).getId());
        assertEquals(ids.get(1), tags.get(1).getId());
        assertEquals(ids.get(2), tags.get(2).getId());

    }

    @Test
    void findTagByTagName() {

        String tagName = "tag_ru13";

        Optional<Tag> tags = underTest.findTagByTagName(tagName);

        assertTrue(tags.isPresent());
        assertEquals(tags.stream().count(), 1);

    }

    @Test
    void findAllByTagNameContainingIgnoreCase(){

        String tagName = "tAg_Ru99";

        List<Tag> tags = underTest.findAllByTagNameContainingIgnoreCase(tagName);

        assertEquals(tags.get(0).getTagName(), "tag_ru99");
    }


}