package com.covenant.tribe.domain;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "tag_name", nullable = false, unique = true, length = 50)
    String tagName;

    @ManyToMany(mappedBy = "tagSet", fetch = FetchType.LAZY)
    @ToString.Exclude
    List<Event> eventListWithTag;

    @ManyToMany(mappedBy = "tagList", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    Set<EventType> eventTypesToWhichTagBelong = new HashSet<>();

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;
        return this.id != null && this.id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
