package com.covenant.tribe.repository;

import com.covenant.tribe.domain.user.UnknownUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnknownUserRepository extends JpaRepository<UnknownUser, Long> {
    UnknownUser findUnknownUserByFirebaseId(String firebaseId);
}
