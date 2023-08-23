package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventStatus;
import com.covenant.tribe.domain.user.Profession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProfessionRepository extends JpaRepository<Profession, Long> {
    Set<Profession> findAllByIdNotIn(List<Long> professionIds);

    boolean existsProfessionByName(String profession);
}
