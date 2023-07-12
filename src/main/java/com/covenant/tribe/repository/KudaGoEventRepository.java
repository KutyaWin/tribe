package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.external.KudaGoEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface KudaGoEventRepository extends JpaRepository<KudaGoEvent, Long> {

    List<Long> findAllHandledEventIdsByEndedAtGreaterThanEqualAndHandledEventId(OffsetDateTime endedAt,List<Long> ids);

}
