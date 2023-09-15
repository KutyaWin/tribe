package com.covenant.tribe.domain.event;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "contact_info")
public class EventContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "contact_type")
    @Enumerated(EnumType.STRING)
    ContactType contactType;

    @Column(name = "contact", columnDefinition = "TEXT")
    String contact;

    @ManyToMany(mappedBy = "eventContactInfos")
    @Builder.Default
    List<Event> events = new ArrayList<>();

}