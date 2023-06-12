package com.covenant.tribe.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email_verification_code")
public class EmailVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "reset_code", nullable = false, length = 4)
    int resetCode;

    @Column(name = "email", nullable = false, length = 100)
    String email;

    @Column(name = "request_time", nullable = false)
    Instant requestTime;

    @Column(name = "is_enable", nullable = false)
    boolean isEnable;

}
