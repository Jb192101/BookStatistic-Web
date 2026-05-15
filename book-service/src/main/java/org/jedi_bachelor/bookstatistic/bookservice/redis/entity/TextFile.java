package org.jedi_bachelor.bookstatistic.bookservice.redis.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("text_file_redis")
public class TextFile implements Serializable {
    @Id
    private String id;

    private String filename;

    private String content;

    private String contentType;

    private long size;

    private LocalDateTime uploadTime;
}
