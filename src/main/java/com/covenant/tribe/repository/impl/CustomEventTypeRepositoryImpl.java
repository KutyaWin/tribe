package com.covenant.tribe.repository.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.repository.CustomEventTypeRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Repository
public class CustomEventTypeRepositoryImpl implements CustomEventTypeRepository {

    EntityManager entityManager;

    @Transactional(readOnly = true)
    @Override
    public Optional<EventType> findEventTypeByIdFetchEventListWithTypeAndTagList(Long eventTypeId) {
        var eventType = entityManager.createQuery(
                        "select distinct et from EventType et left join fetch et.eventListWithType where et.id = ?1",
                        EventType.class)
                .setParameter(1, eventTypeId)
                .getSingleResult();

        eventType = entityManager.createQuery(
                        "select distinct et from EventType et left join fetch et.tagList where et in ?1",
                        EventType.class)
                .setParameter(1, eventType)
                .getSingleResult();

        return Optional.of(eventType);
    }
}
