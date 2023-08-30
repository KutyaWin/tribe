package com.covenant.tribe.domain.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;

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
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_status", nullable = false)
    RelationshipStatus relationshipStatus;

    @Builder.Default
    @Column(name = "subscribe_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime subscribeAt = OffsetDateTime.now();

    @Column(name = "unsubscribe_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime unsubscribeAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_who_made_following", nullable = false)
    @ToString.Exclude
    User userWhoMadeFollowing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_who_get_follower", nullable = false)
    @ToString.Exclude
    User userWhoGetFollower;

    public void unsubscribeUser() {
        unsubscribeAt = OffsetDateTime.now();
        relationshipStatus = RelationshipStatus.UNSUBSCRIBE;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Friendship friendship = (Friendship) o;
        return this.id != null && this.id.equals(friendship.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
