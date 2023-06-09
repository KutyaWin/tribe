package com.covenant.tribe.repository.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.repository.CustomTagRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Repository
public class CustomTagRepositoryImpl implements CustomTagRepository {

    EntityManager entityManager;

    @Transactional(readOnly = true)
    @Override
    public List<Tag> findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(Set<Long> ids) {
        var tags = entityManager.createQuery(
                        "select distinct t from Tag t left join fetch t.eventListWithTag where t.id in (?1)",
                        Tag.class)
                .setParameter(1, ids)
                .getResultList();

        tags = entityManager.createQuery(
                "select distinct t from Tag t left join fetch t.eventTypesToWhichTagBelong where t in (?1)",
                Tag.class)
                .setParameter(1, tags)
                .getResultList();

        return tags;
    }
}
