package com.covenant.tribe.repository.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.QEvent;
import com.covenant.tribe.repository.FilterEventRepository;
import com.covenant.tribe.util.querydsl.EventFilter;
import com.covenant.tribe.util.querydsl.QPredicates;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Repository
public class FilterEventRepositoryImpl implements FilterEventRepository {

    EntityManager entityManager;

    public List<Event> findAllByFilter(EventFilter filter) {
        Predicate predicate = QPredicates.builder()
                .add(filter.getEventTypeIdList(), QEvent.event.eventType.id::in)
                .build();

        return new JPAQuery<Event>(entityManager)
                .select(QEvent.event)
                .from(QEvent.event)
                .where(predicate)
                .fetch();
    }
}
