package com.covenant.tribe.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phone_verification_code")
public class PhoneVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "verification_code", nullable = false, length = 4)
    int verificationCode;

    @Column(name = "phone_number", nullable = false, length = 20)
    String phoneNumber;

    @Column(name = "request_time", nullable = false)
    @Builder.Default
    OffsetDateTime requestTime = OffsetDateTime.now();

    @Column(name = "is_enable", nullable = false)
    @Builder.Default
    boolean isEnable = true;

}
