package com.covenant.tribe.domain.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "registrant")
public class Registrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "email", length = 50)
    String email;

    @Column(name = "phone_number", length = 20)
    String phoneNumber;

    @ToString.Exclude
    @Column(name = "password", length = 500, nullable = false)
    String password;

    @Column(name = "verification_code", length = 4)
    Integer verificationCode;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    Instant createdAt = Instant.now();

    @Column(name = "username", length = 50)
    String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    RegistrantStatus status;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Registrant registrant = (Registrant) o;
        return this.id != null && this.id.equals(registrant.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
