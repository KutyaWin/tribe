package com.covenant.tribe.domain.event;

import com.covenant.tribe.domain.user.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
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
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id")
    @ToString.Exclude
    User organizer;

    @Builder.Default
    @Column(name = "created_at")
    LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_address_id")
    @ToString.Exclude
    EventAddress eventAddress;

    @Column(name = "event_name", length = 100, nullable = false)
    String eventName;

    @Column(name = "event_description")
    String eventDescription;

    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    LocalDateTime endTime;

    @Column(name = "event_avatar", length = 200)
    String eventAvatar;

    Double amount;

    @Column(length = 10)
    String currency;

    @Column(name = "event_active", nullable = false)
    boolean eventActive;

    @Column(name = "eighteen_year_limit", nullable = false)
    boolean eighteenYearLimit;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type")
    @ToString.Exclude
    EventType eventType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_tags",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
    )
    @ToString.Exclude
    Set<EventTag> eventTags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_as_participants_events",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<User> usersAsParticipantsEvent = new ArrayList<>();

    @ManyToMany(mappedBy = "viewedEvents")
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<User> usersWhichViewedEvent = new ArrayList<>();

    @ManyToMany(mappedBy = "favoritesEvent")
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<User> usersWhichAddedEventToFavorite = new ArrayList<>();

    // TODO: add method "addTag"

    public void addUserWhichAddedEventToFavorite(User userWhichAddedEventToFavorite) {
        if (this.usersWhichAddedEventToFavorite == null) this.usersWhichAddedEventToFavorite = new ArrayList<>();

        if (!this.usersWhichAddedEventToFavorite.contains(userWhichAddedEventToFavorite)) {
            this.usersWhichAddedEventToFavorite.add(userWhichAddedEventToFavorite);
            userWhichAddedEventToFavorite.getFavoritesEvent().add(this);
        } else {
            log.error(
                    String.format("There is already user who has added event to the favorite." +
                            "UsersWhichAddedEventToFavorite: %s. Passed User: %s",
                            this.usersWhichAddedEventToFavorite.stream().map(User::getId).toList(), userWhichAddedEventToFavorite.getId()));
            throw new IllegalArgumentException(
                    String.format("There is already user which has added event to the favorite." +
                                    "UsersWhichAddedEventToFavorite: %s. Passed User: %s",
                            this.usersWhichAddedEventToFavorite.stream().map(User::getId).toList(), userWhichAddedEventToFavorite.getId())
            );
        }
    }

    public void addUserWhichViewedEvent(User userWhichViewedEvent) {
        if (this.usersWhichViewedEvent == null) this.usersWhichViewedEvent = new ArrayList<>();

        if (!this.usersWhichViewedEvent.contains(userWhichViewedEvent)) {
            this.usersWhichViewedEvent.add(userWhichViewedEvent);
            userWhichViewedEvent.getViewedEvents().add(this);
        } else {
            log.error(
                    String.format("There's already a user who has viewed event." +
                            "UserWhichViewedEvent: %s. Passed User: %s",
                            this.usersWhichViewedEvent.stream().map(User::getId).toList(), userWhichViewedEvent.getId()));
            throw new IllegalArgumentException(
                    String.format("There's already a user who has viewed event." +
                                    "UserWhichViewedEvent: %s. Passed User: %s",
                            this.usersWhichViewedEvent.stream().map(User::getId).toList(), userWhichViewedEvent.getId())
            );
        }
    }

    public void addUserAsAsParticipantsEvent(User userAsParticipantsEvent) {
        if (this.usersAsParticipantsEvent == null) this.usersAsParticipantsEvent = new ArrayList<>();

        if (!this.usersAsParticipantsEvent.contains(userAsParticipantsEvent)) {
            this.usersAsParticipantsEvent.add(userAsParticipantsEvent);
            userAsParticipantsEvent.getEventsWhereUserAsParticipant().add(this);
        } else {
            log.error(
                    String.format("Event already have passed user as participant event. " +
                                    "UsersAsParticipantsEvent: %s, Passed User: %s",
                    this.usersAsParticipantsEvent.stream().map(User::getId).toList(), userAsParticipantsEvent.getId()));
            throw new IllegalArgumentException(
                    String.format(String.format("Event already have passed user as participant event. " +
                                    "UsersAsParticipantsEvent: %s, Passed User: %s",
                    this.usersAsParticipantsEvent.stream().map(User::getId).toList(), userAsParticipantsEvent.getId())));
        }
    }

    @Override
    public boolean equals(Object o){
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
