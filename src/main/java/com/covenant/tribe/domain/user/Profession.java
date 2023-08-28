package com.covenant.tribe.domain.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profession")
public class Profession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    String name;

    @ManyToMany(mappedBy = "userProfessions", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    Set<User> userWithProfession = new HashSet<>();

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Profession profession = (Profession) o;
        return this.id != null && this.id.equals(profession.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
