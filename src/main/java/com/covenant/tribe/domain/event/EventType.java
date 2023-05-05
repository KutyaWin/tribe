package com.covenant.tribe.domain.event;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.AlreadyExistArgumentForAddToEntityException;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
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

    @Column(name = "type_name", nullable = false, length = 50, unique = true)
    String typeName;

    @Column(columnDefinition = "TEXT", name = "animation_json")
    String animationJson;

    @OneToMany(mappedBy = "eventType", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Event> eventListWithType = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_type_tags",
            joinColumns = {@JoinColumn(name = "type_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", nullable = false)}
    )
    @ToString.Exclude
    Set<Tag> tagList = new HashSet<>();

    @ManyToMany(mappedBy = "interestingEventType", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<User> usersWhoInterestedInEventType = new HashSet<>();

    public void addTag(Tag tag) {
        if (this.tagList == null) this.tagList = new HashSet<>();

        if (!this.tagList.contains(tag)) {
            this.tagList.add(tag);
            tag.getEventTypesToWhichTagBelong().add(this);
        } else {
            String message = String.format("Tag with id: %s is already exist in taglist: %s",
                    tag.getId(),
                    this.tagList
                            .stream()
                            .map(Tag::getId)
                            .toList()
            );
            log.error(message);
            throw new AlreadyExistArgumentForAddToEntityException(message);
        }
    }
    public void addEvent(Event passedEvent) {
        if (this.eventListWithType == null) this.eventListWithType = new ArrayList<>();

        if (!this.eventListWithType.contains(passedEvent)) {
            this.eventListWithType.add(passedEvent);
            passedEvent.setEventType(this);
        } else {
            log.error(
                    String.format("There's already a passed event in the eventListWithType" +
                                    "EventType eventListWithType: %s. Passed passedEvent: %s",
                            this.eventListWithType.stream().map(Event::getId).toList(), passedEvent.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed event in the eventListWithType" +
                                    "EventType eventListWithType: %s. Passed passedEvent: %s",
                            this.eventListWithType.stream().map(Event::getId).toList(), passedEvent.getId())
            );
        }
    }

    @Override
    public boolean equals(Object o) {
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
