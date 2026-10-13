CREATE TABLE IF NOT EXISTS book (
    id BIGINT NOT NULL AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    isbn VARCHAR(20) NULL,
    name VARCHAR(150) NOT NULL,
    author VARCHAR(150) NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    quantity INT NOT NULL,
    total_quantity INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_book_isbn (isbn),
    KEY ix_book_name (name),
    KEY ix_book_active (active)
);

CREATE TABLE IF NOT EXISTS client (
    id BIGINT NOT NULL AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_client_email (email),
    KEY ix_client_name (name),
    KEY ix_client_active (active)
);

CREATE TABLE IF NOT EXISTS loan (
    id BIGINT NOT NULL AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    client_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    returned_date DATE NULL,
    returned BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_loan_client FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_loan_book FOREIGN KEY (book_id) REFERENCES book(id),
    KEY ix_loan_book_returned (book_id, returned),
    KEY ix_loan_client_returned (client_id, returned),
    KEY ix_loan_due_returned (due_date, returned)
);

CREATE TABLE IF NOT EXISTS returned_loan (
    id BIGINT NOT NULL AUTO_INCREMENT,
    client_name VARCHAR(150) NOT NULL,
    client_email VARCHAR(254) NOT NULL,
    book_name VARCHAR(150) NOT NULL,
    book_author VARCHAR(150) NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    returned_date DATE NOT NULL,
    PRIMARY KEY (id),
    KEY ix_returned_loan_date (returned_date)
);

CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_app_user_email (email),
    KEY ix_app_user_active (active)
);

CREATE TABLE IF NOT EXISTS audit_event (
    id BIGINT NOT NULL AUTO_INCREMENT,
    actor VARCHAR(254) NOT NULL,
    action VARCHAR(60) NOT NULL,
    resource_type VARCHAR(60) NOT NULL,
    resource_id BIGINT NULL,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY ix_audit_event_resource (resource_type, resource_id),
    KEY ix_audit_event_occurred_at (occurred_at)
);
