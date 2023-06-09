package com.covenant.tribe.domain.user;


import com.covenant.tribe.domain.event.EventType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "unknown_users")
public class UnknownUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "firebase_id", nullable = false, unique = true, length = 100)
    String firebaseId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "unknown_user_interests",
            joinColumns = {@JoinColumn(name = "unknown_user_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_type_id")}
    )
    @ToString.Exclude
    List<EventType> userInterests = new ArrayList<>();

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        UnknownUser unknownUser = (UnknownUser) o;
        return this.id != null && this.id.equals(unknownUser.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
