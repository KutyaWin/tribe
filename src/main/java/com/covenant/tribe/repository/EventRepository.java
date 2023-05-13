package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventNameAndStartTimeAndOrganizerId(String eventName, OffsetDateTime startTime, Long organizerId);

    List<Event> findAllByOrganizerIdAndEventStatusIsNot(Long organizerId, EventStatus eventStatus);

    List<Event> findAllByEventStatus(EventStatus eventStatus);
}
