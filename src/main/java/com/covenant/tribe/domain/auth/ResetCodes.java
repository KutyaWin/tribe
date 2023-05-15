package com.covenant.tribe.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reset_codes")
public class ResetCodes {

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
