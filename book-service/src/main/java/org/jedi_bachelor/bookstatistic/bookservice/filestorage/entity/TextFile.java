package org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Document(collection = "text_files")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextFile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String filename;

    private String content;

    private String contentType;

    private long size;

    private LocalDateTime uploadTime;
}
