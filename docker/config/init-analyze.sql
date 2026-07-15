CREATE TABLE book_analysis (
);

СREATE TABLE user_book_reports (
    user_id UUID PRIMARY KEY,
    following_genres jsonbs
);

CREATE TABLE outbox_kafka (
);