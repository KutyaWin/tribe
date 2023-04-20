package com.covenant.tribe.domain.event;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.user.UserRelationsWithEvent;
import com.covenant.tribe.exeption.AlreadyExistArgumentForAddToEntityException;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

    @Column(name = "show_event_in_search", nullable = false)
    boolean showEventInSearch;

    @Column(name = "send_to_all_users_by_interests", nullable = false)
    boolean sendToAllUsersByInterests;

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
    @Setter(AccessLevel.PRIVATE)
    Set<Tag> tagSet = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_as_participants_events",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false)
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<User> usersAsParticipantsEvent = new HashSet<>();

    @ManyToMany(mappedBy = "viewedEvents", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<User> usersWhichViewedEvent = new ArrayList<>();

    @ManyToMany(mappedBy = "favoritesEvent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<User> usersWhichAddedEventToFavorite = new ArrayList<>();

    @ManyToMany(mappedBy = "invitationToEvent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<User> usersWhoInvitedToEvent = new HashSet<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<UserRelationsWithEvent> eventRelationsWithUser = new ArrayList<>();

    public void addEventRelationsWithUser(UserRelationsWithEvent userRelationsWithEvent) {
        if (this.eventRelationsWithUser == null) this.eventRelationsWithUser = new ArrayList<>();

        if (!this.eventRelationsWithUser.contains(userRelationsWithEvent)) {
            this.eventRelationsWithUser.add(userRelationsWithEvent);
            userRelationsWithEvent.setEvent(this);
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

    public void addUserWhoInvitedToEvent(User passedUserWhoInvited) {
        if (this.usersWhoInvitedToEvent == null) this.usersWhoInvitedToEvent = new HashSet<>();

        if (!this.usersWhoInvitedToEvent.contains(passedUserWhoInvited)) {
            this.usersWhoInvitedToEvent.add(passedUserWhoInvited);
            passedUserWhoInvited.getInvitationToEvent().add(this);
        } else {
            log.error(
                    String.format("There's already a passed user in the event usersWhoInvitedToEvent." +
                                    "Event usersWhoInvitedToEvent: %s. Passed userWhoInvitedToEvent: %s",
                            usersWhoInvitedToEvent.stream().map(User::getId).toList(), passedUserWhoInvited.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed user in the event usersWhoInvitedToEvent." +
                                    "Event usersWhoInvitedToEvent: %s. Passed userWhoInvitedToEvent: %s",
                            usersWhoInvitedToEvent.stream().map(User::getId).toList(), passedUserWhoInvited.getId())
            );
        }
    }

    public void addUsersWhoInvitedToEvent(Set<User> passedUsersWhoInvited) {
        if (this.usersWhoInvitedToEvent == null) this.usersWhoInvitedToEvent = new HashSet<>();

        if (!this.usersWhoInvitedToEvent.stream()
                .map(User::getUsername)
                .anyMatch(passedUsersWhoInvited::contains)) {

            for (User passedUser : passedUsersWhoInvited) {
                this.usersWhoInvitedToEvent.add(passedUser);
                passedUser.getInvitationToEvent().add(this);
            }
        } else {
            log.error(
                    String.format("There's already a passed users in the event usersWhoInvitedToEvent." +
                                    "Event usersWhoInvitedToEvent: %s. Passed usersWhoInvited: %s",
                            usersWhoInvitedToEvent.stream().map(User::getId).toList(),
                            passedUsersWhoInvited.stream().map(User::getId).toList()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed users in the event usersWhoInvitedToEvent." +
                                    "Event usersWhoInvitedToEvent: %s. Passed usersWhoInvited: %s",
                            usersWhoInvitedToEvent.stream().map(User::getId).toList(),
                            passedUsersWhoInvited.stream().map(User::getId).toList())
            );
        }
    }

    public void addTag(Tag tag) {
        if (this.tagSet == null) this.tagSet = new HashSet<>();

        if (!this.tagSet.contains(tag)) {
            this.tagSet.add(tag);
            tag.getEventListWithTag().add(this);
        } else {
            log.error(
                    String.format("There's already a passed tag in the event tagSet." +
                                    "Event tagSet: %s. Passed tag: %s",
                            this.tagSet.stream().map(Tag::getId).toList(), tag.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed tag in the event tagSet." +
                                    "Event tagSet: %s. Passed tag: %s",
                            this.tagSet.stream().map(Tag::getId).toList(), tag.getId())
            );
        }
    }

    public void addTagSet(Set<Tag> passedTags) {
        if (this.tagSet == null) this.tagSet = new HashSet<>();

        if (!this.tagSet.stream()
                .map(Tag::getTagName)
                .anyMatch(passedTags::contains)) {

            for (Tag tag : passedTags) {
                this.tagSet.add(tag);
                tag.getEventListWithTag().add(this);
            }
        } else {
            log.error(
                    String.format("There is already exist a passed tag in the event tagSet." +
                            "Event tagSet: %s. Passed passedTagSet: %s",
                            this.tagSet.stream().map(Tag::getId).toList(), passedTags.stream().map(Tag::getId).toList())
            );
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There is already exist a passed tag in the event tagSet." +
                                    "Event tagSet: %s. Passed passedTagSet: %s",
                            this.tagSet.stream().map(Tag::getId).toList(), passedTags.stream().map(Tag::getId).toList())
            );
        }
    }

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
            throw new AlreadyExistArgumentForAddToEntityException(
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
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a user who has viewed event." +
                                    "UserWhichViewedEvent: %s. Passed User: %s",
                            this.usersWhichViewedEvent.stream().map(User::getId).toList(), userWhichViewedEvent.getId())
            );
        }
    }

    public void addUserAsAsParticipantsEvent(User userAsParticipantsEvent) {
        if (this.usersAsParticipantsEvent == null) this.usersAsParticipantsEvent = new HashSet<>();

        if (!this.usersAsParticipantsEvent.contains(userAsParticipantsEvent)) {
            this.usersAsParticipantsEvent.add(userAsParticipantsEvent);
            userAsParticipantsEvent.getEventsWhereUserAsParticipant().add(this);
        } else {
            log.error(
                    String.format("Event already have passed user as participant event. " +
                                    "UsersAsParticipantsEvent: %s, Passed User: %s",
                    this.usersAsParticipantsEvent.stream().map(User::getId).toList(), userAsParticipantsEvent.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format(String.format("Event already have passed user as participant event. " +
                                    "UsersAsParticipantsEvent: %s, Passed User: %s",
                    this.usersAsParticipantsEvent.stream().map(User::getId).toList(), userAsParticipantsEvent.getId())));
        }
    }

    public void addUserSetAsParticipants(Set<User> passedUserAsParticipantsEvent) {
        if (this.usersAsParticipantsEvent == null) this.usersAsParticipantsEvent = new HashSet<>();

        if (!this.usersAsParticipantsEvent.stream()
                .map(User::getUsername)
                .anyMatch(passedUserAsParticipantsEvent::contains)) {

            for (User passedUser : passedUserAsParticipantsEvent) {
                this.usersAsParticipantsEvent.add(passedUser);
                passedUser.getEventsWhereUserAsParticipant().add(this);
            }
        } else {
            log.error(
                    String.format("There is already exist a passed user in the event userListParticipants." +
                                    "Event userListParticipants: %s. Passed passedListParticipants: %s",
                            this.usersAsParticipantsEvent.stream().map(User::getId),
                            passedUserAsParticipantsEvent.stream().map(User::getId).toList())
            );
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There is already exist a passed user in the event userListParticipants." +
                                    "Event userListParticipants: %s. Passed passedListParticipants: %s",
                            this.usersAsParticipantsEvent.stream().map(User::getId),
                            passedUserAsParticipantsEvent.stream().map(User::getId).toList())
            );
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
