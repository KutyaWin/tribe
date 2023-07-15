package com.covenant.tribe.domain.event;

import com.covenant.tribe.util.querydsl.PartsOfDay;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "parts_of_day")
public class EventPartOfDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "part_of_day")
    private Integer partsOfDay;

    @ManyToMany(mappedBy = "partsOfDay")
    private List<Event> event;
}