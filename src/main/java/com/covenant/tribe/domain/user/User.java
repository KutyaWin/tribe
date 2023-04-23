package com.covenant.tribe.domain.user;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.exeption.AlreadyExistArgumentForAddToEntityException;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "social_id", unique = true)
    String socialId;

    @Column(name = "firebase_id", nullable = false)
    String firebaseId;

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

    @Column(length = 100, unique = true)
    String username;

    @Column(name = "birthday", columnDefinition = "DATE  ")
    LocalDate birthday;

    @Column(name = "user_avatar", length = 200)
    String userAvatar;

    @Column(name = "bluetooth_id", length = 100, nullable = false)
    String bluetoothId;

    @Column(name = "enable_geolocation", nullable = false)
    boolean enableGeolocation;

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

    @OneToMany(mappedBy = "userWhoMadeFollowing", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<Friendship> following = new HashSet<>();

    @OneToMany(mappedBy = "userWhoGetFollower", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<Friendship> followers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "invitations_to_event_of_users",
        joinColumns = @JoinColumn(name = "user_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "event_id", nullable = false))
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<Event> invitationToEvent = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_interests",
        joinColumns = @JoinColumn(name = "user_id", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "event_type_id", nullable = false))
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<EventType> interestingEventType = new HashSet<>();

    public void addFollowing(Friendship friendship) {
        if (this.following == null) this.following = new HashSet<>();

        if (!this.following.contains(friendship)) {
            this.following.add(friendship);
            friendship.setUserWhoMadeFollowing(this);
        } else {
            log.error(
                    String.format("There's already a passed friendship in the user following." +
                                    "User followings: %s. Passed friendship: %s",
                            this.following.stream().map(Friendship::getId), friendship.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed friendship in the user following." +
                                    "User followings: %s. Passed friendship: %s",
                            this.following.stream().map(Friendship::getId), friendship.getId())
            );
        }
    }

    public void addFollowers(Friendship friendship) {
        if (this.followers == null) this.followers = new HashSet<>();

        if (!this.followers.contains(friendship)) {
            this.followers.add(friendship);
            friendship.setUserWhoGetFollower(this);
        } else {
            log.error(
                    String.format("There's already a passed friendship in the user followers." +
                                    "User followers: %s. Passed friendship: %s",
                            this.followers.stream().map(Friendship::getId), friendship.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed friendship in the user followers." +
                                    "User followers: %s. Passed friendship: %s",
                            this.followers.stream().map(Friendship::getId), friendship.getId())
            );
        }
    }

    public void addInterestingEventType(EventType eventType) {
        if (this.interestingEventType == null) this.interestingEventType = new HashSet<>();

        if (!this.interestingEventType.contains(eventType)) {
            this.interestingEventType.add(eventType);
            eventType.getUsersWhoInterestedInEventType().add(this);
        } else {
            log.error(
                    String.format("There's already a passed eventType in the user interestingEventType." +
                                    "User interestingEventType: %s. Passed eventType: %s",
                            interestingEventType.stream().map(EventType::getId).toList(), eventType.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed eventType in the user interestingEventType." +
                                    "User interestingEventType: %s. Passed eventType: %s",
                            interestingEventType.stream().map(EventType::getId).toList(), eventType.getId())
            );
        }
    }

    public void addInterestingEventTypes(Set<EventType> passedInterestingEventTypes) {
        if (this.interestingEventType == null) this.interestingEventType = new HashSet<>();

        if (!this.interestingEventType.stream()
                .map(EventType::getId)
                .anyMatch(passedInterestingEventTypes::contains)) {

            for (EventType eventType : passedInterestingEventTypes) {
                this.interestingEventType.add(eventType);
                eventType.getUsersWhoInterestedInEventType().add(this);
            }
        } else {
            log.error(
                    String.format("There's already a passed eventType in the user interestingEventType." +
                                    "User interestingEventTypes: %s. Passed eventTypes: %s",
                            interestingEventType.stream().map(EventType::getId).toList(),
                            passedInterestingEventTypes.stream().map(EventType::getId).toList()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed eventType in the user interestingEventType." +
                                    "User interestingEventTypes: %s. Passed eventTypes: %s",
                            interestingEventType.stream().map(EventType::getId).toList(),
                            passedInterestingEventTypes.stream().map(EventType::getId).toList())
            );
        }
    }

    public void addInvitationToEvent(Event passedInvitationToEvent) {
        if (this.invitationToEvent == null) this.invitationToEvent = new HashSet<>();

        if (!this.invitationToEvent.contains(passedInvitationToEvent)) {
            this.invitationToEvent.add(passedInvitationToEvent);
            passedInvitationToEvent.getUsersWhoInvitedToEvent().add(this);
        } else {
            log.error(
                    String.format("There's already a passed event in the user invitationToEvent." +
                                    "User invitationsToEvents: %s. Passed invitationToEvent: %s",
                            invitationToEvent.stream().map(Event::getId).toList(), passedInvitationToEvent.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed event in the user invitationToEvent." +
                                    "User invitationsToEvents: %s. Passed invitationToEvent: %s",
                            invitationToEvent.stream().map(Event::getId).toList(), passedInvitationToEvent.getId())
            );
        }
    }

    public void addInvitationsToEvents(Set<Event> passedInvitationsToEvents) {
        if (this.invitationToEvent == null) this.invitationToEvent = new HashSet<>();

        if (!this.invitationToEvent.stream()
                .map(Event::getId)
                .anyMatch(passedInvitationsToEvents::contains)) {

            for (Event passedEvent : passedInvitationsToEvents) {
                this.invitationToEvent.add(passedEvent);
                passedEvent.getUsersWhoInvitedToEvent().add(this);
            }
        } else {
            log.error(
                    String.format("There's already a passed event in the user invitationToEvent." +
                                    "User invitationsToEvents: %s. Passed invitationsToEvents: %s",
                            invitationToEvent.stream().map(Event::getId).toList(),
                            passedInvitationsToEvents.stream().map(Event::getId).toList()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed event in the user invitationToEvent." +
                                    "User invitationsToEvents: %s. Passed invitationsToEvents: %s",
                            invitationToEvent.stream().map(Event::getId).toList(),
                            passedInvitationsToEvents.stream().map(Event::getId).toList())
            );
        }
    }

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
            throw new AlreadyExistArgumentForAddToEntityException(
                    format("User already has passed event in favorites." +
                                    "User favorites: %s. Passed favorite event: %s",
                            this.favoritesEvent.stream().map(Event::getId).toList(), favoriteEvent.getId())
            );
        }
    }
    public void removeFavoriteEvent(Event favoriteEvent) {
        if (this.favoritesEvent == null) this.favoritesEvent = new ArrayList<>();

        if (!this.favoritesEvent.contains(favoriteEvent)) {
            String message = format("The event cannot be deleted because user with id " +
                    "- %s doesn't have event with id - %s in his favorites.", this.id, favoriteEvent.getId());
            log.error(message);
            throw new IllegalArgumentException(message);
        } else {
            this.favoritesEvent.remove(favoriteEvent);
            favoriteEvent.getUsersAsParticipantsEvent().remove(this);
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
            throw new AlreadyExistArgumentForAddToEntityException(
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
            throw new AlreadyExistArgumentForAddToEntityException(
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
            throw new AlreadyExistArgumentForAddToEntityException(
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
