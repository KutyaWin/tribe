package com.covenant.tribe.domain.event.external;

import com.covenant.tribe.domain.event.Event;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kuda_go_event")
public class KudaGoEvent {

    @Id
    @Column(name = "handled_event_id", nullable = false)
    Long handledEventId;

    @Column(name = "ended_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime endedAt;

    @Column(name = "similarity_percent")
    Double similarityPercent;

    @ManyToMany(mappedBy = "similarEvents")
    List<Event> similarEvents;

}