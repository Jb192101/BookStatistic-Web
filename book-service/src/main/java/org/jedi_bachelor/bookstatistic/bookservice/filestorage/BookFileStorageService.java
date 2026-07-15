package org.jedi_bachelor.bookstatistic.bookservice.filestorage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.repository.TextFileRepository;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.TextNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookFileStorageService {
    private final TextFileRepository textFileRepository;

    /**
     * Сохранение файла в хранилище
     *
     * @param file   файл для сохранения
     * @param bookId ID книги
     * @return TextFile с метаданными
     */
    @Transactional
    public TextFile saveTextFile(MultipartFile file, UUID bookId) throws IOException {
        // 1. Формируем путь к файлу: {storagePath}/{bookId}.txt
        String fileName = bookId.toString() + ".txt";

        // 2. Конвертируем в TextFile
        TextFile textFile = this.createNewTextFileEntity(fileName, new String(file.getBytes(), StandardCharsets.UTF_8), bookId);

        TextFile savedTextFile = this.textFileRepository.save(textFile);

        log.info("File saved: {}", savedTextFile);

        return savedTextFile;
    }

    /**
     * Метод поиска TextFile по ID книги
     */
    @Transactional
    public Optional<TextFile> findTextFileByBookId(UUID bookId) throws TextNotFoundException {
        return this.textFileRepository.findById(bookId.toString());
    }

    /**
     * Метод выдачи всех файлов с текстами
     */
    public List<TextFile> findAll() {
        return (List<TextFile>) this.textFileRepository.findAll();
    }

    @Transactional
    public void deleteById(UUID bookId) {
        this.textFileRepository.deleteById(bookId.toString());
    }

    /**
     * Существует ли текст с таким ID книги
     */
    @Transactional
    public boolean exists(UUID bookId) {
        return this.textFileRepository.existsById(bookId.toString());
    }

    private TextFile createNewTextFileEntity(String fileName, String content, UUID bookId) {
        TextFile textFile = new TextFile();
        textFile.setId(bookId.toString());
        textFile.setFilename(fileName);
        textFile.setContent(content);
        textFile.setContentType("text/plain");
        textFile.setSize(content.getBytes(StandardCharsets.UTF_8).length);
        textFile.setUploadTime(LocalDateTime.now());

        return textFile;
    }

    /**
     * Чтение файла и конвертация в TextFile
     */
    @Deprecated
    private TextFile readTextFileFromPath(Path filePath) {
        try {
            String fileName = filePath.getFileName().toString();
            String content = Files.readString(filePath, StandardCharsets.UTF_8);
            long size = Files.size(filePath);

            return TextFile.builder()
                    .filename(fileName)
                    .content(content)
                    .contentType("text/plain")
                    .size(size)
                    .uploadTime(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            log.error("Failed to read file: {}", filePath, e);
            return null;
        }
    }
}
