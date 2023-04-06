package com.covenant.tribe.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_types")
public class EventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    String name ;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_type_tags",
            joinColumns = {@JoinColumn(name = "type_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    @ToString.Exclude
    Set<EventTag> tagList = new HashSet<>();

}
