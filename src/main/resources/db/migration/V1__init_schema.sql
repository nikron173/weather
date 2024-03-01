CREATE TABLE IF NOT EXISTS users (
    id bigserial primary key,
    login text not null,
    password text not null
);

CREATE TABLE IF NOT EXISTS sessions (
    id text primary key,
    user_id int not null,
    expires_at timestamp not null,
    CONSTRAINT fk_session_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS locations (
    id bigserial primary key,
    name text not null,
    latitude decimal(3,3) not null,
    longitude decimal(3,3) not null
);

CREATE TABLE IF NOT EXISTS users_locations (
    user_id int not null,
    location_id int not null,
    PRIMARY KEY (user_id, location_id),
    CONSTRAINT fk1_users_locations_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk2_users_locations_id FOREIGN KEY (location_id) REFERENCES locations (id)
);

INSERT INTO users (login, password) VALUES ('johnWolt', '3123');
INSERT INTO users (login, password) VALUES ('Kira092', '12322');
INSERT INTO users (login, password) VALUES ('Ursa', '3123123');
INSERT INTO users (login, password) VALUES ('Hyllsa', '871833');
INSERT INTO users (login, password) VALUES ('Pllasst', 'mo213123');