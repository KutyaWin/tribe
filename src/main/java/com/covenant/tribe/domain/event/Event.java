package com.covenant.tribe.domain.event;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.exeption.AlreadyExistArgumentForAddToEntityException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
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
@Table(
        name = "events",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"event_name", "start_time", "organizer_id"}
        )
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id")
    @ToString.Exclude
    User organizer;

    @Builder.Default
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime createdAt = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "event_address_id")
    @ToString.Exclude
    EventAddress eventAddress;

    @Column(name = "event_name", length = 100, nullable = false)
    String eventName;

    @Column(name = "event_description")
    String eventDescription;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    OffsetDateTime endTime;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    Set<EventAvatar> eventAvatars = new HashSet<>();

    @Column(name = "show_event_in_search", nullable = false)
    boolean showEventInSearch;

    @Column(name = "send_to_all_users_by_interests", nullable = false)
    boolean sendToAllUsersByInterests;

    @Column(name = "eighteen_year_limit", nullable = false)
    boolean eighteenYearLimit;

    @Column(name = "is_private", nullable = false)
    boolean isPrivate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type")
    @ToString.Exclude
    EventType eventType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    EventStatus eventStatus = EventStatus.VERIFICATION_PENDING;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_tags",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Tag> tagList = new ArrayList<>();

    @OneToMany(
            mappedBy = "eventRelations",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<UserRelationsWithEvent> eventRelationsWithUser = new ArrayList<>();

    public void addEventAvatar(EventAvatar eventAvatar) {
        if (this.eventAvatars == null) this.eventAvatars = new HashSet<>();
        if (!this.eventAvatars.contains(eventAvatar)) {
            this.eventAvatars.add(eventAvatar);
            eventAvatar.setEvent(this);
        } else {
            String message = String.format(
                    "There's already a passed event avatar in the event. Event eventAvatars: %s. Passed eventAvatar: %s",
                    eventAvatars.stream().map(EventAvatar::getId).toList(),
                    eventAvatar.getId());
            log.error(String.format(message));
            throw new AlreadyExistArgumentForAddToEntityException(message);
        }
    }

    public void addEventRelationsWithUser(UserRelationsWithEvent userRelationsWithEvent) {
        if (this.eventRelationsWithUser == null) this.eventRelationsWithUser = new ArrayList<>();

        if (!this.eventRelationsWithUser.contains(userRelationsWithEvent)) {
            this.eventRelationsWithUser.add(userRelationsWithEvent);
            userRelationsWithEvent.setEventRelations(this);
            if (!userRelationsWithEvent.getUserRelations().getUserRelationsWithEvents().contains(userRelationsWithEvent)) {
                userRelationsWithEvent.getUserRelations().getUserRelationsWithEvents().add(userRelationsWithEvent);
            }
        } else {
            log.error(
                    String.format("There's already a passed user relations with event in the event." +
                                    "Event eventRelationsWithUser: %s. Passed userRelationsWithEvent: %s",
                            eventRelationsWithUser.stream().map(UserRelationsWithEvent::getId).toList(),
                            userRelationsWithEvent.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed user relations with event in the event eventRelationsWithUser." +
                                    "Event eventRelationsWithUser: %s. Passed userRelationsWithEvent: %s",
                            eventRelationsWithUser.stream().map(UserRelationsWithEvent::getId).toList(),
                            userRelationsWithEvent.getId())
            );
        }
    }
    public void addEventsRelationsWithUsers(List<UserRelationsWithEvent> userRelationsWithEvents) {
        if (this.eventRelationsWithUser == null) this.eventRelationsWithUser = new ArrayList<>();
        userRelationsWithEvents.forEach(this::addEventRelationsWithUser);
    }

    public void addTag(Tag tag) {
        if (this.tagList == null) this.tagList = new ArrayList<>();

        if (this.tagList.stream()
                .noneMatch(t -> t.getTagName().equals(tag.getTagName()))) {

            this.tagList.add(tag);
            tag.getEventListWithTag().add(this);

        } else {
            log.error(
                    String.format("There's already a passed tag in the event tagSet." +
                                    "Event tagSet: %s. Passed tag: %s",
                            this.tagList.stream().map(Tag::getId).toList(), tag.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed tag in the event tagSet." +
                                    "Event tagSet: %s. Passed tag: %s",
                            this.tagList.stream().map(Tag::getId).toList(), tag.getId())
            );
        }
    }

    public void addTagList(List<Tag> passedTags) {

        if (this.tagList == null) this.tagList = new ArrayList<>();

        passedTags.forEach(this::addTag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Event event = (Event) o;
        return this.id != null && this.id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
