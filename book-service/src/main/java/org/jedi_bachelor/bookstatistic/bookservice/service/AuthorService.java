package org.jedi_bachelor.bookstatistic.bookservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Author;
import org.jedi_bachelor.bookstatistic.bookservice.entity.BookAuthorRelation;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.AuthorMapper;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.BookAuthorRelationMapper;
import org.jedi_bachelor.bookstatistic.bookservice.repository.AuthorRepository;
import org.jedi_bachelor.bookstatistic.bookservice.repository.BookAuthorRepository;
import org.jedi_bachelor.bookstatistic.bookservice.repository.BookRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.AuthorDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.BookAuthorRelationDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.AuthorCreationUpdatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.BookAuthorRelationKey;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.LinkBookToAuthorTaskDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {
    private final AuthorRepository authorRepository;

    private final BookAuthorRepository bookAuthorRepository;

    private final BookRepository bookRepository;

    private final AuthorMapper authorMapper;

    private final BookAuthorRelationMapper bookAuthorMapper;

    /**
     * Метод добавления нового автора в систему
     *
     * @param dto DTO создания автора
     * @return сущность автора
     */
    public AuthorDto addNewAuthor(AuthorCreationUpdatingDto dto) throws AuthorAlreadyExistsException {
        Optional<Author> authorOptional = this.authorRepository.findByFirstNameAndMiddleNameAndLastName(
                dto.firstName(),
                dto.middleName(),
                dto.lastName()
        );

        if(authorOptional.isPresent()) {
            throw new AuthorAlreadyExistsException(dto.firstName(), dto.middleName(), dto.middleName());
        }

        Author author = new Author();
        author.setFirstName(dto.firstName());
        author.setMiddleName(dto.middleName());
        author.setLastName(dto.lastName());
        author.setBirthday(dto.birthday());
        author.setCountry(dto.country());

        Author savedAuthor = this.authorRepository.save(author);

        return this.authorMapper.toDto(savedAuthor);
    }

    /**
     * Метод удаления автора
     *
     * @param authorId ID автора
     * @return true, если сущность удалена
     */
    public void deleteAuthor(UUID authorId) throws AuthorNotFoundException {
        if(!this.authorRepository.existsById(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }

        List<BookAuthorRelation> relations = this.bookAuthorRepository.findByAuthorId(authorId);

        if(!relations.isEmpty()) {
            log.warn("Author with ID {} have some relations with books", authorId);

            return;
        }

        this.authorRepository.deleteById(authorId);
    }

    /**
     * Метод обновления данных автора
     *
     * @param dto DTO обновления
     * @return автора с обновлёнными данными (для подтверждения)
     */
    public AuthorDto updateAuthor(UUID authorId, AuthorCreationUpdatingDto dto) throws AuthorNotFoundException {
        Optional<Author> author = this.authorRepository.findById(authorId);

        if(author.isEmpty()) {
            throw new AuthorNotFoundException(authorId);
        }

        Author representedAuthor = author.get();
        representedAuthor.setCountry(dto.country());
        representedAuthor.setFirstName(dto.firstName());
        representedAuthor.setMiddleName(dto.middleName());
        representedAuthor.setLastName(dto.lastName());
        representedAuthor.setBirthday(dto.birthday());

        Author savedAuthor = this.authorRepository.save(representedAuthor);

        return this.authorMapper.toDto(savedAuthor);
    }

    public AuthorDto getAuthorById(UUID authorId) throws AuthorNotFoundException {
        if(!this.authorRepository.existsById(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }

        return this.authorMapper.toDto(this.authorRepository.findById(authorId).get());
    }

    public List<AuthorDto> getAllAuthors() {
        List<Author> authors = this.authorRepository.findAll();

        return this.authorMapper.toDtoList(authors);
    }

    public BookAuthorRelationDto linkBookToAuthor(LinkBookToAuthorTaskDto dto)
            throws AuthorNotFoundException, BookNotFoundException,
            BookAuthorRelationAlreadyExistsException {
        if(!this.authorRepository.existsById(dto.authorId())) {
            throw new AuthorNotFoundException(dto.authorId());
        }

        if(!this.bookRepository.existsById(dto.bookId())) {
            throw new BookNotFoundException(dto.bookId());
        }

        if(this.bookAuthorRepository.existsByBook_IdAndAuthor_Id(dto.bookId(), dto.authorId())) {
            throw new BookAuthorRelationAlreadyExistsException(dto.bookId(), dto.bookId());
        }

        BookAuthorRelation relation = new BookAuthorRelation();
        relation.setAuthor(this.authorRepository.findById(dto.authorId()).get());
        relation.setBook(this.bookRepository.findById(dto.bookId()).get());
        relation.setAuthorPosition(dto.authorPosition());

        BookAuthorRelation savedRelation = this.bookAuthorRepository.save(relation);

        return this.bookAuthorMapper.toDto(savedRelation);
    }

    public void deleteBookAuthorRelation(BookAuthorRelationKey key)
            throws BookAuthorRelationNotFoundException {
        if(!this.bookAuthorRepository.existsByBook_IdAndAuthor_Id(key.bookId(), key.authorId())) {
            throw new BookAuthorRelationNotFoundException(key.bookId(), key.authorId());
        }

        this.bookAuthorRepository.deleteByBook_IdAndAuthor_Id(key.bookId(), key.authorId());
    }

    public List<BookAuthorRelationDto> getAllBookAuthorRelations() {
        List<BookAuthorRelation> relations = this.bookAuthorRepository.findAll();

        return this.bookAuthorMapper.toDtoList(relations);
    }
}
