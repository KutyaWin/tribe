package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventPartOfDay;
import com.covenant.tribe.util.querydsl.PartsOfDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventPartOfDayRepository extends JpaRepository<EventPartOfDay, Long> {
    Optional<EventPartOfDay> findByPartsOfDay(Integer partsOfDay);


}