package org.jedi_bachelor.bookstatistic.bookservice.redis;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.redis.entity.TextFile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisContentManager {
    private final RedisTemplate<String, TextFile> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "file:";

    /**
     * Метод для сохранения нового текстового файла
     * Предусловие: уже ясно, что поданный файл является текстовым
     *
     * @param file файл с текстом
     * @return ID новой записи в Redis
     */
    public String saveTextFile(MultipartFile file) throws IOException {
        String fileId = UUID.randomUUID().toString();
        String redisKey = REDIS_KEY_PREFIX + fileId;

        this.redisTemplate.opsForValue().set(redisKey, this.convertFileToEntity(file, redisKey));

        this.redisTemplate.opsForHash().put(redisKey + ":meta", "filename", Objects.requireNonNull(file.getOriginalFilename()));
        this.redisTemplate.opsForHash().put(redisKey + ":meta", "size", String.valueOf(file.getSize()));
        this.redisTemplate.opsForHash().put(redisKey + ":meta", "contentType", Objects.requireNonNull(file.getContentType()));

        return fileId;
    }

    /**
     * Метод выдачи содержимого файла
     *
     * @param fileId ID файла
     * @return содержимое файла
     */
    public TextFile getFileContent(String fileId) {
        String redisKey = REDIS_KEY_PREFIX + fileId;

        return this.redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * Метод для конвертации MultipartFile с содержимым текста в TextFile
     *
     * @param file MultipartFile с текстом
     * @param fileId ID записи в Redis
     * @return новую сущность TextFile
     * @throws IOException если возникут проблемы при чтении данных из MultipartFile
     */
    private TextFile convertFileToEntity(MultipartFile file, String fileId) throws IOException {
        TextFile textFile = new TextFile();
        textFile.setId(fileId);
        textFile.setFilename(file.getOriginalFilename());
        textFile.setContentType(file.getContentType());
        textFile.setSize(file.getSize());
        textFile.setUploadTime(LocalDateTime.now());

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        textFile.setContent(content);

        return textFile;
    }
}
