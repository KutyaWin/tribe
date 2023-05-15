package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByEventNameAndStartTimeAndOrganizerId(String eventName, LocalDateTime startTime, Long organizerId);
}
