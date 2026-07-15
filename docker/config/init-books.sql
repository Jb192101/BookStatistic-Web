CREATE TABLE outbox_notifications (
    id INTEGER PRIMARY KEY,
    published BOOLEAN,
    created_at TIMESTAMP
);

CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(2000)
);

CREATE TABLE users_books (
    user_id UUID,
    book_id UUID,
    readed_pages INTEGER,

    PRIMARY KEY (user_id, book_id)
);

CREATE TABLE response (
    id UUID PRIMARY KEY,
    book_id UUID,
    user_id UUID,
    message TEXT,

    CONSTRAINT fk_book FOREIGN KEY(book_id) REFERENCES book(id) ON DELETE CASCADE
);

CREATE TABLE authors (
);

CREATE TABLE books_authors (
    book_id UUID,
    author_id UUID,
    author_position INTEGER,

    PRIMARY KEY (book_id, author_id);
);

CREATE TABLE text_files (
    id UUID PRIMARY KEY,
    book_id UUID NOT NULL,
    language VARCHAR(255) NOT NULL,
    text_id VARCHAR(255) NOT NULL
);