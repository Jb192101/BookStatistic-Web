package org.jedi_bachelor.bookstatistic.bookservice.entity;

/**
 * Сущность Text является представлением текстового файла в SQL базе данных
 * (в случае этого проекта в Postgres)
 * Используется для связки между данными из реляционной БД и записями в Redis,
 * которые представлены в формате сущностей TextFile
 */

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "text_files")
public class Text {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "text_id")
    private String textFileRedisId;
}
