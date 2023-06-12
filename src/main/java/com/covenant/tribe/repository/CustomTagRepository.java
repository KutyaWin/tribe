package com.covenant.tribe.repository;

import com.covenant.tribe.domain.Tag;

import java.util.List;
import java.util.Set;

public interface CustomTagRepository {
    List<Tag> findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(Set<Long> ids);
}
