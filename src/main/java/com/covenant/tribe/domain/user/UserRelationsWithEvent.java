package com.covenant.tribe.domain.user;

import com.covenant.tribe.domain.event.Event;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friends")
public class UserRelationsWithEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    UserStatus userStatus;

    @Column(name = "is_favorite_event", nullable = false)
    boolean isFavoriteEvent;

    @Column(name = "is_viewed_event", nullable = false)
    boolean isViewedEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    Event event;

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        UserRelationsWithEvent userRelationsWithEvent = (UserRelationsWithEvent) o;
        return this.id != null && this.id.equals(userRelationsWithEvent.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
