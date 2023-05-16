package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventType;
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
            "AND f.userWhoMadeFollowing.username LIKE %:username%")
    Page<User> findAllSubscribers(@Param("userId") Long userId, @Param("username") String username, Pageable pageable);

    @Query("SELECT u.id " +
            "FROM User u " +
            "JOIN u.followers f " +
            "WHERE  f.userWhoMadeFollowing.id = :userId " +
            "AND f.userWhoGetFollower.id IN (:userIds)")
    Set<Long> findMutuallySubscribed(@Param("userIds") List<Long> userIds, @Param("userId") Long userId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByUserEmail(String email);

    List<User> findAllByInterestingEventType(EventType eventType);

    User findBySocialId(String socialId);

    boolean existsUserByUserEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String phoneNumber);

}
