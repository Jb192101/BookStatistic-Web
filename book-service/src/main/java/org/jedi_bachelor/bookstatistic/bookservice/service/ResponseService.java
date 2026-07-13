package org.jedi_bachelor.bookstatistic.bookservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.converter.ResponseConverter;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Response;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Stars;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.ResponseMapper;
import org.jedi_bachelor.bookstatistic.bookservice.repository.BookRepository;
import org.jedi_bachelor.bookstatistic.bookservice.repository.ResponseRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.ResponseDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.ResponseCreateUpdateDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.BookNotFoundException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.ResponseAlreadyExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.ResponseNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResponseService {
    private final ResponseRepository responseRepository;

    private final ResponseMapper responseMapper;

    private final ResponseConverter responseConverter;

    private final BookRepository bookRepository;

    /**
     * Метод добавления нового отзыва о книге
     *
     * @param dto DTO создания отзыва
     * @return DTO созданного отзыва
     * @throws ResponseAlreadyExistsException если отзыв с таким ключом уже есть
     * @throws BookNotFoundException если книги с таким ID нет
     */
    @Transactional(rollbackOn = Exception.class)
    public ResponseDto addNewResponse(ResponseCreateUpdateDto dto) throws ResponseAlreadyExistsException, BookNotFoundException {
        // Если такой книги нет
        if(!this.bookRepository.existsById(dto.bookId())) {
            throw new BookNotFoundException(dto.bookId());
        }

        // Проверка на существование отзыва с такими ID
        if(this.responseRepository.existsById(new Response.ResponseId(dto.userId(), dto.bookId()))) {
            throw new ResponseAlreadyExistsException(dto.userId(), dto.bookId());
        }

        Response response = this.responseConverter.convert(dto);

        this.responseRepository.save(response);

        return this.responseMapper.toDto(response);
    }

    /**
     * Метод выдачи отзыва о книге по ID
     *
     * @param bookId ID книги
     * @param userId ID пользователя
     * @return отзыв, если он есть
     * @throws ResponseNotFoundException если отзыва нет
     */
    @Transactional(rollbackOn = Exception.class)
    public ResponseDto getResponse(UUID bookId, UUID userId) throws ResponseNotFoundException {
        Optional<Response> responseOptional = this.responseRepository.findById(new Response.ResponseId(userId, bookId));

        if(responseOptional.isEmpty()) {
            throw new ResponseNotFoundException(userId, bookId);
        }

        return this.responseMapper.toDto(responseOptional.get());
    }

    /**
     * Метод выдачи всех отзывов в системе
     * @return все отзывы
     */
    @Transactional(rollbackOn = Exception.class)
    public List<ResponseDto> getAllResponses() {
        return this.responseMapper.toDtoList(
                this.responseRepository.findAll()
        );
    }

    /**
     * Метод выдачи всех отзывов по ID книги
     *
     * @param bookId ID книги
     * @return список отзывов, принадлежащих этой книге
     */
    @Transactional(rollbackOn = Exception.class)
    public List<ResponseDto> getAllResponsesOnBook(UUID bookId) {
        List<Response> responses = this.responseRepository.findAll()
                .stream().filter(e -> e.getBookId().equals(bookId)).toList();

        return this.responseMapper.toDtoList(responses);
    }

    @Transactional(rollbackOn = Exception.class)
    public List<ResponseDto> getAllResponsesOfUser(UUID userId) {
        List<Response> responses = this.responseRepository.findAll()
                .stream().filter(e -> e.getUserId().equals(userId)).toList();

        return this.responseMapper.toDtoList(responses);
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDto updateResponse(ResponseCreateUpdateDto dto) throws ResponseNotFoundException {
        if(!this.responseRepository.existsById(new Response.ResponseId(dto.userId(), dto.bookId()))) {
            throw new ResponseNotFoundException(dto.userId(), dto.bookId());
        }

        Response response = this.responseRepository.getReferenceById(new Response.ResponseId(dto.userId(), dto.bookId()));
        response.setResponseText(dto.responseText());
        response.setStars(Stars.from(dto.countOfStars()));
        response.setUpdatedAt(LocalDateTime.now());

        this.responseRepository.save(response);

        return this.responseMapper.toDto(response);
    }

    @Transactional(rollbackOn = Exception.class)
    public ResponseDto deleteResponse(UUID bookId, UUID userId) throws ResponseNotFoundException {
        if(!this.responseRepository.existsById(new Response.ResponseId(userId, bookId))) {
            throw new ResponseNotFoundException(userId, bookId);
        }

        Response response = this.responseRepository.findById(new Response.ResponseId(userId, bookId)).get();

        this.responseRepository.delete(response);

        return this.responseMapper.toDto(response);
    }
}