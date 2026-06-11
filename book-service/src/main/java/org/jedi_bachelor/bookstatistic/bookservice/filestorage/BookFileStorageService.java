package org.jedi_bachelor.bookstatistic.bookservice.filestorage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.TextNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookFileStorageService {

    @Value("${book.storage.path:/data/books}")
    private String storagePath;

    /**
     * Сохранение файла в хранилище
     *
     * @param file   файл для сохранения
     * @param bookId ID книги
     * @return TextFile с метаданными
     */
    @Transactional
    public TextFile saveTextFile(MultipartFile file, UUID bookId) throws IOException {
        // 1. Создаём директорию, если её нет (одна общая директория)
        Path dir = Paths.get(storagePath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // 2. Формируем путь к файлу: {storagePath}/{bookId}.txt
        String fileName = bookId.toString() + ".txt";
        Path filePath = dir.resolve(fileName);

        // 3. Сохраняем файл на диск
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Конвертируем в TextFile
        TextFile textFile = this.createNewTextFileEntity(fileName, new String(file.getBytes(), StandardCharsets.UTF_8));

        log.info("File saved: {}", filePath);

        return textFile;
    }

    /**
     * Сохранение текстового содержимого напрямую (без MultipartFile)
     */
    @Transactional
    public TextFile saveTextContent(String content, UUID bookId) throws IOException {
        Path dir = Paths.get(storagePath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String fileName = bookId.toString() + ".txt";
        Path filePath = dir.resolve(fileName);

        Files.writeString(filePath, content, StandardCharsets.UTF_8);

        TextFile textFile = this.createNewTextFileEntity(fileName, content);

        log.info("Text content saved: {}", filePath);

        return textFile;
    }

    /**
     * Метод выдачи всех файлов с текстами
     */
    public List<TextFile> findAll() {
        List<TextFile> documents = new ArrayList<>();
        Path dir = Paths.get(storagePath);

        if (!Files.exists(dir)) {
            return documents;
        }

        try (Stream<Path> files = Files.list(dir)) {
            documents = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .map(this::readTextFileFromPath)
                    .filter(textFile -> textFile != null)
                    .toList();
        } catch (IOException e) {
            log.error("Failed to list all text files", e);
        }

        return documents;
    }

    /**
     * Существует ли текст с таким ID книги
     */
    @Transactional
    public boolean exists(UUID bookId) {
        String fileName = bookId.toString() + ".txt";

        Path filePath = Paths.get(storagePath, fileName);

        return Files.exists(filePath);
    }

    /**
     * Метод удаления текста по ID книги
     */
    @Transactional
    public boolean deleteByBookId(UUID bookId) {
        String fileName = bookId.toString() + ".txt";
        Path filePath = Paths.get(storagePath, fileName);

        try {
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("File with book ID {} has been deleted", bookId);
            }
            return deleted;
        } catch (IOException e) {
            log.error("Failed to delete file for book ID: {}", bookId, e);
            return false;
        }
    }

    /**
     * Метод поиска TextFile по ID книги
     */
    @Transactional
    public TextFile findTextFileByBookId(UUID bookId) throws TextNotFoundException {
        String fileName = bookId.toString() + ".txt";
        Path filePath = Paths.get(storagePath, fileName);

        if (!Files.exists(filePath)) {
            throw new TextNotFoundException(bookId);
        }

        return readTextFileFromPath(filePath);
    }

    /**
     * Получение содержимого файла в виде строки
     */
    public String readFileContent(UUID bookId) throws TextNotFoundException {
        String fileName = bookId.toString() + ".txt";
        Path filePath = Paths.get(storagePath, fileName);

        if (!Files.exists(filePath)) {
            throw new TextNotFoundException(bookId);
        }

        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to read file content: {}", filePath, e);
            throw new TextNotFoundException(bookId);
        }
    }

    /**
     * Чтение файла и конвертация в TextFile
     */
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

    private TextFile createNewTextFileEntity(String fileName, String content) {
        TextFile textFile = new TextFile();
        textFile.setFilename(fileName);
        textFile.setContent(content);
        textFile.setContentType("text/plain");
        textFile.setSize(content.getBytes(StandardCharsets.UTF_8).length);
        textFile.setUploadTime(LocalDateTime.now());

        return textFile;
    }
}