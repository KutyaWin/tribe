package com.covenant.tribe.domain.event;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_avatars")
public class EventAvatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    Event event;

    @Column(name = "avatar_url", length = 200)
    String avatarUrl;

}
