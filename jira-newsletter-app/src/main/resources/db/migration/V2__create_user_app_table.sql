CREATE TABLE user_app
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email      VARCHAR(255)                            NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    gender     VARCHAR(255)                            NOT NULL,
    password   VARCHAR(255)                            NOT NULL,
    role       VARCHAR(255),
    CONSTRAINT pk_user_app PRIMARY KEY (id)
);

ALTER TABLE user_app
    ADD CONSTRAINT uc_user_app_email UNIQUE (email);