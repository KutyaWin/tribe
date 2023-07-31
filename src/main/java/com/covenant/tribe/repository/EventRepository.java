package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByEventNameAndStartTimeAndOrganizerId(String eventName, OffsetDateTime startTime, Long organizerId);

    List<Event> findAllByOrganizerIdAndEventStatusIsNot(Long organizerId, EventStatus eventStatus);

    List<Event> findAllByOrganizerIdAndEventStatusIs(Long organizerId, EventStatus eventStatus);

    List<Event> findAllByEventStatus(EventStatus eventStatus);

    Optional<Event> findByOrganizerIdAndId(Long organizerId, Long eventId);

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
}
