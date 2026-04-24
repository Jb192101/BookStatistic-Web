package org.jedi_bachelor.bookstatistic.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("text_file")
public class TextFile {
    @Id
    private String id;

    private String filename;

    private String content;

    private String contentType;

    private long size;

    private LocalDateTime uploadTime;

    private String owner;

    private String description;
}
