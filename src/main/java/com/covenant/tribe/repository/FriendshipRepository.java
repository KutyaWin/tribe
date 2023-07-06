package com.covenant.tribe.repository;

import com.covenant.tribe.domain.user.Friendship;
import com.covenant.tribe.domain.user.RelationshipStatus;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUserWhoGetFollowerAndUserWhoMadeFollowingAndRelationshipStatus(
            User userWhoGetFollower, User userWhoMadeFollowing, RelationshipStatus relationshipStatus
    );

    Optional<Friendship> findByUserWhoMadeFollowingAndUserWhoGetFollowerAndUnsubscribeAtIsNull(
            User follower, User following
    );

    @Modifying
    @Query(
            "UPDATE Friendship f SET f.relationshipStatus = :relationshipStatus, " +
                    "f.unsubscribeAt = :unsubscribeAt " +
                    "WHERE f.userWhoGetFollower.id = :userId " +
                    "OR f.userWhoMadeFollowing.id = :userId "
    )
    void unsubscribeAll(
            @Param("relationshipStatus") RelationshipStatus relationshipStatus,
            @Param("userId") Long userId,
            @Param("unsubscribeAt") OffsetDateTime unsubscribeAt
    );
}