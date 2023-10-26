package com.covenant.tribe.chat.domain;

import com.covenant.tribe.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Table(name = "last_read_message")
@AllArgsConstructor
public class LastReadMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    Chat chat;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    User participant;

    @OneToOne
    @JoinColumn(name = "message_id")
    Message message;

}