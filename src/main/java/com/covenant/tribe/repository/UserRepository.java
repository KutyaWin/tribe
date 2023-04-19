package com.covenant.tribe.repository;

import com.covenant.tribe.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserById(Long id);

    Optional<User> findUserByUsername(String username);

    boolean existsUserByUserEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByPhoneNumber(String phoneNumber);
}
