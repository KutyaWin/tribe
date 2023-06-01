package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.RelationshipStatus;
import com.covenant.tribe.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserById(Long id);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:partialUsername%")
    Page<User> findAllByUsernameContains(@Param("partialUsername") String partialUsername, Pageable pageable);
    @Query("SELECT f.userWhoMadeFollowing " +
            "FROM User u " +
            "JOIN u.followers f " +
            "WHERE f.userWhoGetFollower.id = :userId " +
            "AND f.userWhoMadeFollowing.username LIKE %:username% " +
            "AND f.relationshipStatus = :relationshipStatus")
    Page<User> findAllSubscribersByPartialUsername
            (@Param("userId") Long userId,
             @Param("username") String username,
             @Param("relationshipStatus") RelationshipStatus relationshipStatus,
             Pageable pageable);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u NOT IN (" +
                "SELECT f.userWhoMadeFollowing " +
                "FROM User u " +
                "JOIN u.followers f " +
                "WHERE f.userWhoGetFollower.id = :userId " +
                "AND f.relationshipStatus = :relationshipStatus" +
            ")" +
            "AND u.id <> :userId")
    Page<User> findAllNotFollowingUser(
            @Param("userId") Long userId,
            @Param("relationshipStatus") RelationshipStatus relationshipStatus,
            Pageable pageable);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u " +
            "NOT IN (" +
            "SELECT f.userWhoMadeFollowing " +
            "FROM User u " +
            "JOIN u.followers f " +
            "WHERE f.userWhoGetFollower.id = :userId " +
            "AND f.relationshipStatus = :relationshipStatus) " +
            "AND u.id <> :userId " +
            "AND u.username LIKE :unsubscriberUsername%")
    Page<User> findAllNotFollowingUserByPartialUsername(
            String unsubscriberUsername, long userId, RelationshipStatus relationshipStatus, Pageable pageable
    );


    @Query("SELECT f.userWhoMadeFollowing " +
            "FROM User u " +
            "JOIN u.followers f " +
            "WHERE f.userWhoGetFollower.id = :userId AND f.relationshipStatus = :relationshipStatus")
    Page<User> findAllSubscribers(
            @Param("userId") Long userId,
            @Param("relationshipStatus") RelationshipStatus relationshipStatus,
            Pageable pageable);

    @Query("SELECT u.id " +
            "FROM User u " +
            "JOIN u.followers f " +
            "WHERE  f.userWhoMadeFollowing.id = :userId " +
            "AND f.userWhoGetFollower.id IN (:userIds)")
    Set<Long> findMutuallySubscribed(@Param("userIds") List<Long> userIds, @Param("userId") Long userId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByUserEmail(String email);

    User findUserByPhoneNumber(String phoneNumber);

    List<User> findAllByInterestingEventType(EventType eventType);

    User findByGoogleId(String googleId);
    User findByVkId(String vkId);

    boolean existsUserByUserEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String phoneNumber);

}
