package com.covenant.tribe.domain.event;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.exeption.AlreadyExistArgumentForAddToEntityException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @Column(name = "created_at", columnDefinition = "TIMESTAMP", nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "event_address_id")
    @ToString.Exclude
    EventAddress eventAddress;

    @Column(name = "event_name", length = 100, nullable = false)
    String eventName;

    @Column(name = "event_description", columnDefinition = "TEXT")
    String eventDescription;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    LocalDateTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    LocalDateTime endTime;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    Set<EventAvatar> eventAvatars = new HashSet<>();

    @Column(name = "show_event_in_search", nullable = false)
    boolean showEventInSearch;

    @Column(name = "send_to_all_users_by_interests", nullable = false)
    boolean sendToAllUsersByInterests;

    @Column(name = "is_eighteen_year_limit", nullable = false, columnDefinition = "boolean default false")
    boolean isEighteenYearLimit;

    @Column(name = "is_private", nullable = false)
    boolean isPrivate;

    @Column(name = "is_presence_of_alcohol", nullable = false, columnDefinition = "boolean default false")
    boolean isPresenceOfAlcohol;

    @Column(name = "is_free", nullable = false, columnDefinition = "boolean default false")
    boolean isFree;

    @Column(name = "is_start_time_updated", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    boolean isStartTimeUpdated = false;

    @Column(name = "is_from_kudago", columnDefinition = "boolean default false")
    @Builder.Default
    boolean isFromKudaGo = false;

    @Column(name = "external_publication_date")
    @Builder.Default
    LocalDate externalPublicationDate = null;

    @Column(name = "kudago_id")
    @Builder.Default
    Long kudaGoId = null;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type")
    @ToString.Exclude
    EventType eventType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    EventStatus eventStatus = EventStatus.VERIFICATION_PENDING;

    @Column(name = "time_zone", columnDefinition = "varchar")
    String timeZone;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_tags",
            joinColumns = @JoinColumn(name = "event_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_events_tags_event_id",
                            foreignKeyDefinition = "FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE")),
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    List<Tag> tagList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "event_part_of_day",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "part_of_day_id")
    )
    private Set<EventPartOfDay> partsOfDay;

    @OneToMany(
            mappedBy = "eventRelations",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
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

    public void addEventAvatars(Set<EventAvatar> eventAvatars) {
        eventAvatars.forEach(this::addEventAvatar);
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

    public void deleteAvatars(List<EventAvatar> eventAvatars) {
        eventAvatars.forEach(this::deleteAvatar);
    }

    public void deleteAvatar(EventAvatar eventAvatar) {
        eventAvatar.setEvent(null);
        this.eventAvatars.remove(eventAvatar);
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
