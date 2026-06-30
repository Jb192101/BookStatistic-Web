package org.jedi_bachelor.bookstatistic.bookservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Entity
@Table(name = "books")
@Data
@Audited
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    @Audited
    private String title;

    @Column(name = "description")
    @Audited
    private String description;

    @Column(name = "pages")
    @Audited
    private Integer pages = 0;
}
