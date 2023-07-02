package com.covenant.tribe.scheduling.model;

import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.scheduling.notifications.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "sent_date")
    Date sentDate;

    @Column(name = "message_text", nullable = false)
    String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    NotificationStatus status = NotificationStatus.NEW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User userById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broadcast_id", referencedColumnName = "id", nullable = false)
    BroadcastEntity broadcastEntity;

    public Notification(String text, User userById, BroadcastEntity broadcastEntity) {
        this.text = text;
        this.userById = userById;
        this.broadcastEntity = broadcastEntity;
    }

}
