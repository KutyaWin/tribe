package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long>, CustomEventTypeRepository {

    Optional<EventType> findEventTypeByTypeName(String typeName);
}
