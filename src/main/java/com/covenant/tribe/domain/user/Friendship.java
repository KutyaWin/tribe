package com.covenant.tribe.domain.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

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
    @Column(name = "created_at", nullable = false)
    Instant createdAt = Instant.now();

    @Column(name = "added_to_friends_at")
    LocalDateTime addedToFriends_At;

    @Column(name = "added_to_block_at")
    LocalDateTime addedToBlock_At;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_owner_id")
    User relationshipOwnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    User friendId;

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
