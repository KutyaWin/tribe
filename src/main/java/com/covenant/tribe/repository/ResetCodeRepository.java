package com.covenant.tribe.repository;

import com.covenant.tribe.domain.auth.ResetCodes;
import com.covenant.tribe.domain.event.EventAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetCodeRepository extends JpaRepository<ResetCodes, Long> {
    ResetCodes findByEmailAndIsEnable(String email, boolean isEnable);
}
