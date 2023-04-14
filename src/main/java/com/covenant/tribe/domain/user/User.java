package com.covenant.tribe.domain.user;

import com.covenant.tribe.domain.event.Event;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Builder.Default
    @Column(name = "created_at")
    Instant createdAt = Instant.now();

    @Column(name = "user_email", length = 50, nullable = false, unique = true)
    String userEmail;

    @Column(length = 500, nullable = false)
    String password;

    @Column(name = "phone_number", length = 17, unique = true)
    String phoneNumber;

    @Column(name = "first_name", length = 100)
    String firstName;

    @Column(name = "last_name", length = 100)
    String lastName;

    @Column(length = 100, nullable = false, unique = true)
    String username;

    LocalDate birthday;

    @Column(name = "user_avatar", length = 100)
    String userAvatar;

    @Column(name = "bluetooth_id", length = 100, nullable = false)
    String bluetoothId;

    @OneToMany(mappedBy = "organizer", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Event> eventsWhereUserAsOrganizer = new ArrayList<>();

    @ManyToMany(mappedBy = "usersAsParticipantsEvent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Event> eventsWhereUserAsParticipant = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "viewed_user_events",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "event_id", nullable = false))
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Event> viewedEvents = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "favorites",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "event_id", nullable = false))
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Event> favoritesEvent = new ArrayList<>();

    @OneToMany(mappedBy = "relationshipOwnerId", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Friendship> friendsList = new ArrayList<>();

    @OneToMany(mappedBy = "friendId", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Friendship> listFriendshipWhereUserIsFriend = new ArrayList<>();

    public void addFriendshipWhereUserIsFriend(Friendship friendship) {
        if (this.listFriendshipWhereUserIsFriend == null) this.listFriendshipWhereUserIsFriend = new ArrayList<>();

        if (!this.listFriendshipWhereUserIsFriend.contains(friendship)) {
            this.listFriendshipWhereUserIsFriend.add(friendship);
            friendship.setFriendId(this);
        } else {
            log.error(
                    format("user already has friendship where his as a friend." +
                            "User listFriendshipWhereUserIsFriend: %s. Passed Friendship: %s",
                            this.listFriendshipWhereUserIsFriend.stream().map(Friendship::getId).toList(), friendship.getId())
            );
            throw new IllegalArgumentException(
                    format("user already has friendship where his as a friend." +
                                    "User listFriendshipWhereUserIsFriend: %s. Passed Friendship: %s",
                            this.listFriendshipWhereUserIsFriend.stream().map(Friendship::getId).toList(), friendship.getId())
            );
        }
    }

    public void addFriend(Friendship friendship) {
        if (this.friendsList == null) this.friendsList = new ArrayList<>();

        if (!this.friendsList.contains(friendship)) {
            this.friendsList.add(friendship);
            friendship.setRelationshipOwnerId(this);
        } else {
            log.error(
                    format("User already has passed friend" +
                            "User friendsList: %s. Passed friend: %s",
                            this.friendsList.stream().map(Friendship::getId).toList(), friendship.getFriendId())
            );
            throw new IllegalArgumentException(
                    format("User already has passed friend" +
                                    "User friendsList: %s. Passed friend: %s",
                            this.friendsList.stream().map(Friendship::getId).toList(), friendship.getFriendId())
            );
        }
    }

    @Transactional
    public void addFavoriteEvent(Event favoriteEvent) {
        if (this.favoritesEvent == null) this.favoritesEvent = new ArrayList<>();

        if (!this.favoritesEvent.contains(favoriteEvent)) {
            this.favoritesEvent.add(favoriteEvent);
            favoriteEvent.getUsersWhichAddedEventToFavorite().add(this);
        } else {
            log.error(
                    format("User already has passed event in favorites." +
                            "User favorites: %s. Passed favorite event: %s",
                            this.favoritesEvent.stream().map(Event::getId).toList(), favoriteEvent.getId()));
            throw new IllegalArgumentException(
                    format("User already has passed event in favorites." +
                                    "User favorites: %s. Passed favorite event: %s",
                            this.favoritesEvent.stream().map(Event::getId).toList(), favoriteEvent.getId())
            );
        }
    }

    public void addViewedEvent(Event viewedEvent) {
        if (this.viewedEvents == null) this.viewedEvents = new ArrayList<>();

        if (!this.viewedEvents.contains(viewedEvent)) {
            this.viewedEvents.add(viewedEvent);
            viewedEvent.getUsersWhichViewedEvent().add(this);
        } else {
            log.error(
                    format("User already hase viewed passed event." +
                            "User viewedEvents: %s. Passed event: %s",
                            this.viewedEvents.stream().map(Event::getId).toList(), viewedEvent.getId()));
            throw new IllegalArgumentException(
                    format("User already hase viewed passed event." +
                                    "User viewedEvents: %s. Passed event: %s",
                            this.viewedEvents.stream().map(Event::getId).toList(), viewedEvent.getId())
            );
        }
    }

    public void addEventWhereUserAsParticipant(Event eventWhereUserAsParticipant) {
        if (this.eventsWhereUserAsParticipant == null) this.eventsWhereUserAsParticipant = new ArrayList<>();

        if (!this.eventsWhereUserAsParticipant.contains(eventWhereUserAsParticipant)) {
            this.eventsWhereUserAsParticipant.add(eventWhereUserAsParticipant);
            eventWhereUserAsParticipant.getUsersAsParticipantsEvent().add(this);
        } else {
            log.error(
                    format("User already have event where he is participant. " +
                                    "User eventWhereUserAsParticipant: %s, Passed eventWhereUserAsParticipant: %s",
                    this.eventsWhereUserAsParticipant.stream().map(Event::getId).toList(),
                    eventWhereUserAsParticipant.getId()));
            throw new IllegalArgumentException(
                    format("User already have event where he is participant. " +
                                    "User eventWhereUserAsParticipant: %s, Passed eventWhereUserAsParticipant: %s",
                    this.eventsWhereUserAsParticipant.stream().map(Event::getId).toList(),
                    eventWhereUserAsParticipant.getId()));
        }
    }

    public void addEventWhereUserAsOrganizer(Event eventWhereUserAsOrganizer) {
        if (this.eventsWhereUserAsOrganizer == null) this.eventsWhereUserAsOrganizer = new ArrayList<>();

        if (!this.eventsWhereUserAsOrganizer.contains(eventWhereUserAsOrganizer)) {
            this.eventsWhereUserAsOrganizer.add(eventWhereUserAsOrganizer);
            eventWhereUserAsOrganizer.setOrganizer(this);
        } else {
            log.error(
                    format("User already have event with same id. User events: %s, Passed event: %s",
                    this.eventsWhereUserAsOrganizer.stream().map(Event::getId).toList(),
                    eventWhereUserAsOrganizer.getId()
                    ));
            throw new IllegalArgumentException(
                    format("User already have event with same id. User events: %s, Passed event: %s",
                            this.eventsWhereUserAsOrganizer.stream().map(Event::getId).toList(),
                            eventWhereUserAsOrganizer.getId()));
        }
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        User user = (User) o;
        return this.id != null && this.id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
