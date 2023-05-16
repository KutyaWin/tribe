package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.util.querydsl.EventFilter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilterEventRepository {

    List<Event> findAllByFilter(EventFilter filter);
}
