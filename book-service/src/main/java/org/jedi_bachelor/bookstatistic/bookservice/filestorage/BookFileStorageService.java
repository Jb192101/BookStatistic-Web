package org.jedi_bachelor.bookstatistic.bookservice.filestorage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.filestorage.entity.TextFile;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.TextNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookFileStorageService {
    @Value("${book.storage.path}")
    private String storagePath;

    /**
     * Сохранение файла в хранилище
     *
     * @return
     */
    @Transactional
    public TextFile saveTextFile(MultipartFile file, UUID bookId) throws IOException {
        TextFile textFile = this.convertFileToEntity(file, bookId);

        return textFile;
    }

    /**
     * Метод выдачи всех файлов с текстами
     *
     * @return список текстов
     */
    public List<TextFile> findAll() {
        List<TextFile> documents = new ArrayList<>();
        Path rootPath = Paths.get(this.storagePath);

        if (!Files.exists(rootPath)) {
            return documents;
        }

        try (Stream<Path> bookDirs = Files.list(rootPath)) {
            //documents = bookDirs.collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list all text files", e);
        }

        return documents;
    }

    /**
     * Существует ли текст с таким названием
     *
     * @param bookId ID книги
     * @return true, если существует
     */
    @Transactional
    public boolean exists(UUID bookId) throws TextNotFoundException {
        return this.findByFilename(bookId.toString()) != null;
    }

    /**
     * Метод удаления текста по ID книги
     *
     * @param bookId ID книги
     * @return true, если сущность удалена
     */
    @Transactional
    public boolean deleteByFilename(UUID bookId) throws TextNotFoundException {
        File file = this.findByFilename(bookId.toString());
        boolean deleted = file.delete();

        if(deleted) {
            log.info("File with book ID {} has deleted", bookId);
        }

        return deleted;
    }

    /**
     * Метод поиска файла по его названию
     *
     * @param filename название файла
     * @return файл, если он есть
     */
    @Transactional
    public File findByFilename(String filename) throws TextNotFoundException {
        return null;
    }

    /**
     * Метод для конвертации MultipartFile с содержимым текста в TextFile
     *
     * @param file MultipartFile с текстом
     * @param bookId ID книги
     * @return новую сущность TextFile
     * @throws IOException если возникут проблемы при чтении данных из MultipartFile
     */
    private TextFile convertFileToEntity(MultipartFile file, UUID bookId) throws IOException {
        TextFile textFile = new TextFile();
        textFile.setFilename(bookId.toString());
        textFile.setContentType(file.getContentType());
        textFile.setSize(file.getSize());
        textFile.setUploadTime(LocalDateTime.now());

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        textFile.setContent(content);

        return textFile;
    }
}
