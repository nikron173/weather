CREATE TABLE users
(
    id       bigserial primary key,
    login    text not null unique,
    email    text not null unique,
    password text not null
);

CREATE TABLE sessions
(
    id         text primary key,
    user_id    int       not null,
    expires_at timestamp not null,
    CONSTRAINT fk_session_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE locations
(
    id        bigserial primary key,
    name      text    not null,
    country   text    not null,
    state     text,
    latitude  decimal not null,
    longitude decimal not null
);

CREATE TABLE users_locations
(
    user_id int not null,
    location_id int not null,
    PRIMARY KEY (
        user_id,
        location_id
    ),
    CONSTRAINT fk1_users_locations_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk2_users_locations_id FOREIGN KEY (location_id) REFERENCES locations (id)
);

INSERT INTO users (login, email, password)
VALUES ('johnWolt', 'johnwolt@kk.com', '3123');
INSERT INTO users (login, email, password)
VALUES ('Kira092', 'kira092@kk.com', '12322');
INSERT INTO users (login, email, password)
VALUES ('Ursa', 'ursa@kk.com', '3123123');
INSERT INTO users (login, email, password)
VALUES ('Hyllsa', 'hyllsa@kk.com', '871833');
INSERT INTO users (login, email, password)
VALUES ('Pllasst', 'pllasst@kk.com', 'mo213123');