package com.covenant.tribe.domain;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.user.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;

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
@Table(name = "users_relations_with_events")
public class UserRelationsWithEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    @Builder.Default
    UserStatus userStatus = UserStatus.NONE;

    @Column(name = "favorite_event", nullable = false)
    boolean favoriteEvent;

    @Column(name = "viewed_event", nullable = false)
    boolean viewedEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Event event;

    public void setUser(User user) {
        if (this.user != null) {
            this.user.getUserRelationsWithEvents().remove(this);
        }
        this.user = user;
        if (!user.getUserRelationsWithEvents().contains(this)) {
            user.getUserRelationsWithEvents().add(this);
        }
    }

    public void setEvent(Event event) {
        if (this.event != null) {
            this.event.getEventRelationsWithUser().remove(this);
        }
        this.event = event;
        if (!event.getEventRelationsWithUser().contains(this)) {
            event.getEventRelationsWithUser().add(this);
        }
    }

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
