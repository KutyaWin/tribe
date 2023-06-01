package com.covenant.tribe.repository;

import com.covenant.tribe.domain.user.Registrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrantRepository extends JpaRepository<Registrant, Long> {
    Registrant findByEmail(String email);
    Registrant findByPhoneNumber(String phoneNumber);
}