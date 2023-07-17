package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.external.KudaGoEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public interface KudaGoEventRepository extends JpaRepository<KudaGoEvent, Long> {

    @Query(
            """
                SELECT k.handledEventId
                FROM KudaGoEvent k
                WHERE k.publicationDate IN (:now)
                AND k.handledEventId IN (:newEventIds)
            """
    )
    List<Long> findAllRepeatableEventIds(Set<Long> newEventIds, List<LocalDate> now);

}
