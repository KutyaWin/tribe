package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserById(Long id);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:partialUsername%")
    Page<User> findAllByUsernameContains(@Param("partialUsername") String partialUsername, Pageable pageable);

    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByUserEmail(String email);
    List<User> findAllByInterestingEventType(EventType eventType);

    User findBySocialId(String socialId);

    boolean existsUserByUserEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String phoneNumber);
}
