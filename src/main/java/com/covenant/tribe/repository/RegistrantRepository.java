package com.covenant.tribe.repository;

import com.covenant.tribe.domain.user.Registrant;
import com.covenant.tribe.domain.user.RegistrantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrantRepository extends JpaRepository<Registrant, Long> {
    Registrant findFirstByEmailAndStatusOrderByCreatedAtDesc(String email, RegistrantStatus registrantStatus);
    Registrant findByPhoneNumberAndStatus(String phoneNumber, RegistrantStatus registrantStatus);
}