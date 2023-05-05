package com.covenant.tribe.domain;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "UserRelationsWithEvent")
@Table(name = "users_relations_with_events")
public class UserRelationsWithEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "is_invited", nullable = false)
    boolean isInvited;

    @Column(name ="is_participant", nullable = false)
    boolean isParticipant;

    @Column(name = "is_want_to_go", nullable = false)
    boolean isWantToGo;

    @Column(name = "is_favorite", nullable = false)
    boolean isFavorite;

    @Column(name = "is_viewed", nullable = false)
    boolean isViewed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ToString.Exclude
    User userRelations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @ToString.Exclude
    Event eventRelations;

    public void setUserRelations(User userRelations) {
        if (this.userRelations != null) {
            this.userRelations.getUserRelationsWithEvents().remove(this);
        }
        this.userRelations = userRelations;
        if (!userRelations.getUserRelationsWithEvents().contains(this)) {
            userRelations.getUserRelationsWithEvents().add(this);
        }
    }

    public void setEventRelations(Event eventRelations) {
        if (this.eventRelations != null) {
            this.eventRelations.getEventRelationsWithUser().remove(this);
        }
        this.eventRelations = eventRelations;
        if (!eventRelations.getEventRelationsWithUser().contains(this)) {
            eventRelations.getEventRelationsWithUser().add(this);
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
