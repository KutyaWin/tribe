package com.covenant.tribe.repository;

import com.covenant.tribe.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findTagByTagName(String tagName);

    List<Tag> findAllByTagNameContainingIgnoreCase(String tagName);
}
