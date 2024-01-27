DROP TABLE IF EXISTS hit CASCADE;

CREATE TABLE IF NOT EXISTS hit (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY UNIQUE,
    app	VARCHAR(255) NOT NULL,
    uri	VARCHAR(255) NOT NULL,
    ip	VARCHAR(255) NOT NULL,
    time_stamp TIMESTAMP WITHOUT TIME ZONE
    );