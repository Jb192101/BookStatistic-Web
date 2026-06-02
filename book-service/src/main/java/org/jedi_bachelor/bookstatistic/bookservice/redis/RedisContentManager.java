package org.jedi_bachelor.bookstatistic.bookservice.redis;

import lombok.RequiredArgsConstructor;
import org.jedi_bachelor.bookstatistic.bookservice.redis.entity.TextFile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class RedisContentManager {
    private final RedisTemplate<String, TextFile> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "text:document:";

    /**
     * Метод для сохранения нового текстового файла
     * Предусловие: уже ясно, что поданный файл является текстовым
     *
     * @param bookId ID книги
     * @param file файл с текстом
     * @return ID новой записи в Redis
     */
    public String saveTextFile(UUID bookId, MultipartFile file) throws IOException {
        String redisKey = REDIS_KEY_PREFIX + bookId;

        this.redisTemplate.opsForValue().set(redisKey, this.convertFileToEntity(file, redisKey));

        this.redisTemplate.opsForHash().put(redisKey + ":meta", "filename", Objects.requireNonNull(file.getOriginalFilename()));
        this.redisTemplate.opsForHash().put(redisKey + ":meta", "size", String.valueOf(file.getSize()));
        this.redisTemplate.opsForHash().put(redisKey + ":meta", "contentType", Objects.requireNonNull(file.getContentType()));

        return redisKey;
    }

    /**
     * Метод выдачи содержимого файла
     *
     * @param bookId ID файла
     * @return содержимое файла
     */
    public TextFile getTextFile(UUID bookId) {
        String redisKey = REDIS_KEY_PREFIX + bookId;

        return this.redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * Метод выдачи содержимого файла
     *
     * @param key ключ
     * @return содержимое файла
     */
    public TextFile getTextFile(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    /**
     * Метод выдачи всех файлов с текстами
     *
     * @return список текстов
     */
    public List<TextFile> findAll() {
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
        List<TextFile> documents = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                Object obj = redisTemplate.opsForValue().get(key);
                if (obj instanceof TextFile) {
                    documents.add((TextFile) obj);
                }
            }
        }
        return documents;
    }

    /**
     * Существует ли текст с таким ID
     *
     * @param id ID текста
     * @return true, если существует
     */
    public boolean exists(String id) {
        String key = REDIS_KEY_PREFIX + id;
        return Boolean.TRUE.equals(this.redisTemplate.hasKey(key));
    }

    /**
     * Метод удаления текста по ID
     *
     * @param id ID текста
     * @return true, если сущность удалена
     */
    public boolean deleteById(String id) {
        String key = REDIS_KEY_PREFIX + id;
        return Boolean.TRUE.equals(this.redisTemplate.delete(key));
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
