package com.covenant.tribe.domain.event;

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
public class  EventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false, length = 50, unique = true)
    String name;

    @Column(columnDefinition = "TEXT", name = "animation_json")
    String animationJson;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_type_tags",
            joinColumns = {@JoinColumn(name = "type_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false)}
    )
    @ToString.Exclude
    Set<EventTag> tagList = new HashSet<>();

    @OneToMany(mappedBy = "eventType", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Event> eventListWithType = new ArrayList<>();

    // TODO: add method "addTagToTagList"

    // TODO: add method "addEvent"

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        EventType eventType = (EventType) o;
        return this.id != null && this.id.equals(eventType.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
