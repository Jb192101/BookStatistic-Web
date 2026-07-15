CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE notification_settings(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE NOT NULL,
    enable_email BOOLEAN NOT NULL DEFAULT FALSE,
    enable_broadcasting NOT NULL DEFAULT FALSE,
    email VARCHAR(255),
    telegram_address VARCHAR(255),
    enable_telegram NOT NULL DEFAULT FALSE
);

CREATE TABLE notifications(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    type VARCHAR(255) NOT NULL CHECK(type IN ('SYSTEM_ONLY', 'EMAIL_ONLY', 'EMAIL_AND_SYSTEM')),
    notification_title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL
);

CREATE TABLE outbox_email(
    id INTEGER PRIMARY KEY,
    notification_id UUID NOT NULL,
    subject VARCHAR(255),
    message TEXT,
    email_address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    broadcasted BOOLEAN DEFAULT FALSE,
    published BOOLEAN DEFAULT FALSE
);

CREATE TABLE outbox_kafka(
    id INTEGER PRIMARY KEY,
    notification_id UUID NOT NULL,
    title VARCHAR(255),
    message_result TEXT,
    published BOOLEAN DEFAULT FALSE
);