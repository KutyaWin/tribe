package com.covenant.tribe.domain.user;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.exeption.AlreadyExistArgumentForAddToEntityException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
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

    @Column(name = "firebase_id", nullable = false, columnDefinition = "TEXT")
    String firebaseId;

    @Builder.Default
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "user_email", unique = true)
    String userEmail;

    @Column(length = 100)
    String password;

    @Column(name = "phone_number", unique = true)
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

    @Column(name = "enable_geolocation", nullable = false)
    boolean enableGeolocation;

    @Column(name = "has_email_authentication", columnDefinition = "BOOLEAN")
    @Accessors(fluent = true)
    @Builder.Default
    Boolean hasEmailAuthentication = false;

    @Column(name = "has_google_authentication", columnDefinition = "BOOLEAN")
    @Accessors(fluent = true)
    @Builder.Default
    Boolean hasGoogleAuthentication = false;

    @Column(name = "has_vk_authentication", columnDefinition = "BOOLEAN")
    @Accessors(fluent = true)
    @Builder.Default
    Boolean hasVkAuthentication = false;

    @Column(name = "has_whatsapp_authentication", columnDefinition = "BOOLEAN")
    @Accessors(fluent = true)
    @Builder.Default
    Boolean hasWhatsappAuthentication = false;

    @Column(name = "has_telegram_authentication", columnDefinition = "BOOLEAN")
    @Accessors(fluent = true)
    @Builder.Default
    Boolean hasTelegramAuthentication = false;

    @Column(name = "google_id", unique = true)
    String googleId;

    @Column(name = "vk_id", unique = true)
    String vkId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    UserStatus status = UserStatus.ENABLED;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    UserRole userRole = UserRole.USER;

    @Column(name = "is_online", columnDefinition = "boolean default false")
    @Builder.Default
    boolean isOnline = true;

    @OneToMany(
            mappedBy = "organizer",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    List<Event> eventsWhereUserAsOrganizer = new ArrayList<>();

    @OneToMany(
            mappedBy = "userWhoMadeFollowing",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    Set<Friendship> following = new HashSet<>();

    @OneToMany(
            mappedBy = "userWhoGetFollower",
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    Set<Friendship> followers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "event_type_id", nullable = false))
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    Set<EventType> interestingEventType = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_profession",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "profession_id", nullable = false)
    )
    @ToString.Exclude
    @Builder.Default
    Set<Profession> userProfessions = new HashSet<>();

    @ManyToMany(mappedBy = "participant")
    @ToString.Exclude
    Set<Chat> chats;

    @OneToMany(
            mappedBy = "userRelations",
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    @Builder.Default
    List<UserRelationsWithEvent> userRelationsWithEvents = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    Set<Authority> authorities;


    public void addNewProfessions(Set<Profession> professions) {
        if (this.userProfessions == null) {
            this.userProfessions = professions;
        } else {
            this.userProfessions.addAll(professions);
        }
    }

    public void retainProfessions(Set<Profession> professions) {
        if (this.userProfessions == null) {
            this.userProfessions = professions;
        } else {
            this.userProfessions.retainAll(professions);
            this.userProfessions.addAll(professions);
        }
    }

    public void addUserRelationsWithEvent(UserRelationsWithEvent userRelationsWithEvent) {
        if (this.userRelationsWithEvents == null) this.userRelationsWithEvents = new ArrayList<>();

        if (!this.userRelationsWithEvents.contains(userRelationsWithEvent)) {
            this.userRelationsWithEvents.add(userRelationsWithEvent);
            userRelationsWithEvent.setUserRelations(this);
            if (!userRelationsWithEvent.getEventRelations().getEventRelationsWithUser().contains(userRelationsWithEvent)) {
                userRelationsWithEvent.getEventRelations().getEventRelationsWithUser().add(userRelationsWithEvent);
            }
        } else {
            log.error(
                    String.format("There's already a passed userRelationsWithEvent in the User." +
                                    "User userRelationsWithEvents: %s. Passed userRelationsWithEvent: %s",
                            this.userRelationsWithEvents.stream().map(UserRelationsWithEvent::getId),
                            userRelationsWithEvent.getId()));
            throw new AlreadyExistArgumentForAddToEntityException(
                    String.format("There's already a passed userRelationsWithEvent in the user userRelationsWithEvents." +
                                    "User userRelationsWithEvents: %s. Passed userRelationsWithEvent: %s",
                            this.userRelationsWithEvents.stream().map(UserRelationsWithEvent::getId),
                            userRelationsWithEvent.getId())
            );
        }
    }
    public void addUsersRelationsWithEvent(List<UserRelationsWithEvent> userRelationsWithEvents) {
        if (this.userRelationsWithEvents == null) this.userRelationsWithEvents = new ArrayList<>();

        userRelationsWithEvents.forEach(this::addUserRelationsWithEvent);
    }

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
        if (this.interestingEventType == null) {
            this.interestingEventType = passedInterestingEventTypes;
        } else {
            this.interestingEventType.retainAll(passedInterestingEventTypes);
            this.interestingEventType.addAll(passedInterestingEventTypes);
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

    public String getFullName() {
        if (this.firstName == null && this.lastName == null) {
            return "";
        }
        if (this.firstName == null) {
            return this.lastName;
        }
        if (this.lastName == null) {
            return firstName;
        }
        return this.firstName + " " + this.lastName;
    }

    public Integer getAge() {
        if (birthday == null) {
            return null;
        }
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    @Override
    public boolean equals(Object o) {
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
