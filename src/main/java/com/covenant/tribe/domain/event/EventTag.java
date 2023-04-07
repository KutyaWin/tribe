package com.covenant.tribe.domain.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_tags")
public class EventTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    String name;

    @ManyToMany(mappedBy = "tagList", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<EventType> listType = new ArrayList<>();

    @ManyToMany(mappedBy = "eventTags", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Event> eventListWithTag;

    // TODO: add method addTagToEventListWithTag

    // TODO: add method addTypeToListType

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        EventTag eventTag = (EventTag) o;
        return this.id != null && this.id.equals(eventTag.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
