package org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextFile implements Serializable {
    private String filename; // ID книги в String

    private String content;

    private String contentType;

    private long size;

    private LocalDateTime uploadTime;
}
