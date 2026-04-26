CREATE TABLE outbox-notification (
    id INTEGER PRIMARY KEY,
    published BOOLEAN,
    created_at TIMESTAMP
);

CREATE TABLE books (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(2000)
);

CREATE TABLE user_book(
    user_id UUID,
    book_id UUID,
    readed_pages INTEGER,

    PRIMARY KEY(user_id, book_id)
);

CREATE TABLE response (
    id UUID PRIMARY KEY,
    book_id UUID,
    user_id UUID,
    message TEXT

    CONSTRAINT fk_book FOREIGN KEY(book_id) REFERENCES book(id) ON DELETE CASCADE
)