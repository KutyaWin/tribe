package com.covenant.tribe.chat.domain;

import com.covenant.tribe.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

import java.time.ZonedDateTime;

@Entity
@Table(name = "message")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "text", length = 1024)
    String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    @ToString.Exclude
    Chat chat;

    @Column(name = "created_at")
    @TimeZoneStorage(TimeZoneStorageType.NATIVE)
    @Builder.Default
    ZonedDateTime createdAt = ZonedDateTime.now();

}
