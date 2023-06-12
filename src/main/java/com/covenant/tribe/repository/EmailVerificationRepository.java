package com.covenant.tribe.repository;

import com.covenant.tribe.domain.auth.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationCode, Long> {
    EmailVerificationCode findByEmailAndIsEnable(String email, boolean isEnable);
}
