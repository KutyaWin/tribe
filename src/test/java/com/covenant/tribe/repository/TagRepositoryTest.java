package com.covenant.tribe.repository;

import com.covenant.tribe.AbstractTestcontainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql("/sql/init_tags.sql")
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private TagRepository underTest;

    @Test
    void existsByTagName_isShouldCheckIfTagExist() {
        //given
        var preparedTag = "test7198";

        //when
        boolean expected = underTest.existsByTagName(preparedTag).isPresent();

        //then
        assertTrue(expected);
    }
}