package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventAvatarRepository extends JpaRepository<EventAvatar, Long> {
    Integer deleteAllByAvatarUrlIn(List<String> avatarUrls);

    List<EventAvatar> findAllByAvatarUrlIn(List<String> avatarUrls);
}
