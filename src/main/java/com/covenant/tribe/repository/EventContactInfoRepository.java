package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.ContactType;
import com.covenant.tribe.domain.event.EventContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventContactInfoRepository extends JpaRepository<EventContactInfo, Long> {

    Optional<EventContactInfo> findByContactAndContactType(String contact, ContactType contactType);

}
