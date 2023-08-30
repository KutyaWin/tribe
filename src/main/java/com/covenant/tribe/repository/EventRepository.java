package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByEventNameAndStartTimeAndOrganizerId(String eventName, LocalDateTime startTime, Long organizerId);

    Optional<Event> findByEventNameAndStartTime(String eventName, LocalDateTime startTime);

    List<Event> findAllByOrganizerIdAndEventStatusIsNot(Long organizerId, EventStatus eventStatus);

    List<Event> findAllByOrganizerIdAndEventStatusIs(Long organizerId, EventStatus eventStatus);

    List<Event> findAllByEventStatus(EventStatus eventStatus);

    Optional<Event> findByOrganizerIdAndId(Long organizerId, Long eventId);

    List<Event> findAllByEventNameIn(Set<String> eventNames);

    List<Event> findAllByExternalPublicationDateBetween(LocalDate from, LocalDate to);


    @Query(
            """
                        SELECT k.kudaGoId
                        FROM Event k
                        WHERE k.externalPublicationDate >= (:externalPublicationDate)
                        AND (k.kudaGoId IN (:kudaGoIds))
                    """
    )
    List<Long> findAllRepeatableEventIds(

            LocalDate externalPublicationDate,
            Set<Long> kudaGoIds
    );

    Page<Event> findAllByIdIn(List<Long> ids, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);
}
