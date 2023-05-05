package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventAvatarRepository extends JpaRepository<Event, Long> {
}
