package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventNameAndStartTimeAndOrganizerId(String eventName, LocalDateTime startTime, Long organizerId);
}
