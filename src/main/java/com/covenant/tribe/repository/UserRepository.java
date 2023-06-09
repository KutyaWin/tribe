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

    @Query("select distinct u from User u left join fetch u.eventsWhereUserAsOrganizer where u.id = :userId")
    Optional<User> findUserByIdFetchEventsWhereUserAsOrganizer(@Param("userId") Long userId);

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

    List<User> findAllByInterestingEventType(EventType eventType);

    @Query(value = "SELECT * FROM users AS u " +
            "LEFT JOIN user_interests ui on u.id = ui.user_id WHERE ui.event_type_id = ?1", nativeQuery = true)
    List<User> findAllByInterestingEventTypeContaining(Long eventTypeId);

    User findBySocialId(String socialId);

    boolean existsUserByUserEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String phoneNumber);

}
