package com.covenant.tribe.repository;

import com.covenant.tribe.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, CustomTagRepository {

    @Query(value = "select t from Tag t where t.tagName = :tagName")
    Tag findByTagName(@Param("tagName") String tagName);

    List<Tag> findByIdIn(List<Long> ids);

    @Query(value = """
                select t.tagName from Tag t where t.tagName in (:tagNames)
            """)
    Set<String> findAllByTagNames(Set<String> tagNames);

    Set<Tag> findAllByTagNameIn(Set<String> tagNames);

    Optional<Tag> findTagByTagName(String tagName);

    List<Tag> findAllByTagNameContainingIgnoreCase(String tagName);

    @Query(value = "SELECT EXISTS(SELECT * FROM tags WHERE tag_name = :tagName OR tag_name_en = :tagName)" +
            " AS tags_exists", nativeQuery = true)
    boolean existsByTagName(String tagName);
}
