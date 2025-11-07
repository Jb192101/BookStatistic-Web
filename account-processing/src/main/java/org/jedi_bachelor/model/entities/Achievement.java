package org.jedi_bachelor.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Table(name="achievements")
@RequiredArgsConstructor
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title")
    private final String title;

    @Column(name="description")
    private final String description;
}
