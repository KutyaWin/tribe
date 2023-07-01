package com.covenant.tribe.scheduling.model;

import com.covenant.tribe.scheduling.BroadcastStatuses;
import com.covenant.tribe.scheduling.message.MessageStrategyName;
import com.covenant.tribe.scheduling.notifications.NotificationStrategyName;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "broadcasts")
public class BroadcastEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "subject_id", nullable = false)
    Long subjectId;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime startTime;

    @Column(name = "repeat_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime repeatTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime endTime;

    @Column(name = "status", nullable = false)
    BroadcastStatuses status;

    @Column(name = "notifications_created", nullable = false)
    Boolean notificationsCreated;

    @Column(name = "notification_strategy_name")
    @Enumerated(EnumType.STRING)
    NotificationStrategyName notificationStrategyName;

    @Column(name = "message_strategy_name")
    @Enumerated(EnumType.STRING)
    MessageStrategyName messageStrategyName;

    @Column(name = "fire_count")
    @Builder.Default
    Integer fireCount = 0;

    @OneToMany(mappedBy = "broadcastEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Notification> notifications;

}
