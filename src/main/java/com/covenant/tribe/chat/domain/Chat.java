package com.covenant.tribe.chat.domain;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "chat")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "is_group")
    Boolean isGroup;

    @OneToMany(
            mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true
    )
    @ToString.Exclude
    @Builder.Default
    Set<Message> messages = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "chat_participant",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @ToString.Exclude
    @Builder.Default
    Set<User> participant = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "event_id")
    @ToString.Exclude
    Event event;

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addParticipant(User user) {
        participant.add(user);
        user.addChat(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isGroup, messages, participant);
    }
}
