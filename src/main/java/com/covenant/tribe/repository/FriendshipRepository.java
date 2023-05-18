package com.covenant.tribe.repository;

import com.covenant.tribe.domain.user.Friendship;
import com.covenant.tribe.domain.user.RelationshipStatus;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUserWhoGetFollowerAndUserWhoMadeFollowingAndRelationshipStatus(
            User userWhoGetFollower, User userWhoMadeFollowing, RelationshipStatus relationshipStatus
    );

    Optional<Friendship> findByUserWhoMadeFollowingAndUserWhoGetFollower(User follower, User following);
}