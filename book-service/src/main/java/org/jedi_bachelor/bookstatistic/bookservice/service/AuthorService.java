package org.jedi_bachelor.bookstatistic.bookservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jedi_bachelor.bookstatistic.bookservice.entity.Author;
import org.jedi_bachelor.bookstatistic.bookservice.entity.BookAuthorRelation;
import org.jedi_bachelor.bookstatistic.bookservice.mapper.AuthorMapper;
import org.jedi_bachelor.bookstatistic.bookservice.repository.AuthorRepository;
import org.jedi_bachelor.bookstatistic.bookservice.repository.BookAuthorRepository;
import org.jedi_bachelor.bookstatistic.commonslib.dto.mapentities.AuthorDto;
import org.jedi_bachelor.bookstatistic.commonslib.dto.request.book.AuthorCreationUpdatingDto;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.AuthorAlreadyExistsException;
import org.jedi_bachelor.bookstatistic.commonslib.exceptions.AuthorNotFoundException;
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

    private final AuthorMapper authorMapper;

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
        Optional<Author> author = this.authorRepository.findById(authorId);

        if(author.isEmpty()) {
            throw new AuthorNotFoundException(authorId);
        }

        return this.authorMapper.toDto(author.get());
    }

    public List<AuthorDto> getAllAuthors() {
        List<Author> authors = this.authorRepository.findAll();

        return this.authorMapper.toDtoList(authors);
    }
}
