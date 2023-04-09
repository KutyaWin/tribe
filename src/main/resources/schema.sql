-- После добавления механизма миграций удалить все ALTER TABLE DROP CONSTRAINT IF EXISTS
CREATE TABLE IF NOT EXISTS events
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    organizer_id        BIGINT,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    event_address_id    BIGINT,
    event_name          VARCHAR(100)                            NOT NULL,
    event_description   VARCHAR(255),
    start_time          TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_time            TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_avatar        VARCHAR(200),
    amount              DOUBLE PRECISION,
    currency            VARCHAR(10),
    event_active        BOOLEAN                                 NOT NULL,
    eighteen_year_limit BOOLEAN                                 NOT NULL,
    event_type          BIGINT,
    CONSTRAINT pk_events PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events_tags
(
    event_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    CONSTRAINT pk_events_tags PRIMARY KEY (event_id, tag_id)
);

CREATE TABLE IF NOT EXISTS users_as_participants_events
(
    event_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL
);

CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS event_addresses
(
    id              BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_latitude  DOUBLE PRECISION                        NOT NULL,
    event_longitude DOUBLE PRECISION                        NOT NULL,
    event_position  GEOMETRY(Point, 4326)                   NOT NULL,
    city            VARCHAR(100)                            NOT NULL,
    region          VARCHAR(100),
    street          VARCHAR(100),
    district        VARCHAR(100),
    building        VARCHAR(10),
    house_number    VARCHAR(10),
    floor           VARCHAR(10),
    CONSTRAINT pk_event_addresses PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS event_type_tags
(
    tag_id  BIGINT NOT NULL,
    type_id BIGINT NOT NULL,
    CONSTRAINT pk_event_type_tags PRIMARY KEY (tag_id, type_id)
    );

CREATE TABLE IF NOT EXISTS event_types
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_event_types PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS friends
(
    id                    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    relationship_status   VARCHAR(255)                            NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    added_to_friends_at   TIMESTAMP WITHOUT TIME ZONE,
    added_to_block_at     TIMESTAMP WITHOUT TIME ZONE,
    relationship_owner_id BIGINT,
    friend_id             BIGINT,
    CONSTRAINT pk_friends PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS favorites
(
    event_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    user_email   VARCHAR(50)                             NOT NULL,
    password     VARCHAR(500)                            NOT NULL,
    phone_number VARCHAR(17),
    first_name   VARCHAR(100),
    last_name    VARCHAR(100),
    username     VARCHAR(100)                            NOT NULL,
    birthday     TIMESTAMP WITHOUT TIME ZONE,
    user_avatar  VARCHAR(100),
    bluetooth_id VARCHAR(100)                            NOT NULL,
    gender       VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS viewed_user_events
(
    event_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS event_tags
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_event_tags PRIMARY KEY (id)
    );

ALTER TABLE events
    DROP CONSTRAINT IF EXISTS FK_EVENTS_ON_EVENT_ADDRESS;
ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_EVENT_ADDRESS FOREIGN KEY (event_address_id) REFERENCES event_addresses (id);

ALTER TABLE events
DROP CONSTRAINT IF EXISTS FK_EVENTS_ON_EVENT_TYPE;
ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_EVENT_TYPE FOREIGN KEY (event_type) REFERENCES event_types (id);

ALTER TABLE events
DROP CONSTRAINT IF EXISTS FK_EVENTS_ON_ORGANIZER;
ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_ORGANIZER FOREIGN KEY (organizer_id) REFERENCES users (id);

ALTER TABLE events_tags
DROP CONSTRAINT IF EXISTS fk_evetag_on_event;
ALTER TABLE events_tags
    ADD CONSTRAINT fk_evetag_on_event FOREIGN KEY (event_id) REFERENCES events (id);

ALTER TABLE events_tags
DROP CONSTRAINT IF EXISTS fk_evetag_on_event_tag;
ALTER TABLE events_tags
    ADD CONSTRAINT fk_evetag_on_event_tag FOREIGN KEY (tag_id) REFERENCES event_tags (id);

ALTER TABLE users_as_participants_events
DROP CONSTRAINT IF EXISTS fk_useaspareve_on_event;
ALTER TABLE users_as_participants_events
    ADD CONSTRAINT fk_useaspareve_on_event FOREIGN KEY (event_id) REFERENCES events (id);

ALTER TABLE users_as_participants_events
DROP CONSTRAINT IF EXISTS fk_useaspareve_on_user;
ALTER TABLE users_as_participants_events
    ADD CONSTRAINT fk_useaspareve_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE event_types
DROP CONSTRAINT IF EXISTS uc_event_types_name;
ALTER TABLE event_types
    ADD CONSTRAINT uc_event_types_name UNIQUE (name);

ALTER TABLE event_type_tags
DROP CONSTRAINT IF EXISTS fk_evetyptag_on_event_tag;
ALTER TABLE event_type_tags
    ADD CONSTRAINT fk_evetyptag_on_event_tag FOREIGN KEY (tag_id) REFERENCES event_tags (id);

ALTER TABLE event_type_tags
DROP CONSTRAINT IF EXISTS fk_evetyptag_on_event_type;
ALTER TABLE event_type_tags
    ADD CONSTRAINT fk_evetyptag_on_event_type FOREIGN KEY (type_id) REFERENCES event_types (id);

ALTER TABLE friends
DROP CONSTRAINT IF EXISTS FK_FRIENDS_ON_FRIEND;
ALTER TABLE friends
    ADD CONSTRAINT FK_FRIENDS_ON_FRIEND FOREIGN KEY (friend_id) REFERENCES users (id);

ALTER TABLE friends
DROP CONSTRAINT IF EXISTS FK_FRIENDS_ON_RELATIONSHIP_OWNER;
ALTER TABLE friends
    ADD CONSTRAINT FK_FRIENDS_ON_RELATIONSHIP_OWNER FOREIGN KEY (relationship_owner_id) REFERENCES users (id);

ALTER TABLE users
DROP CONSTRAINT IF EXISTS uc_users_phone_number;
ALTER TABLE users
    ADD CONSTRAINT uc_users_phone_number UNIQUE (phone_number);

ALTER TABLE users
DROP CONSTRAINT IF EXISTS uc_users_user_email;
ALTER TABLE users
    ADD CONSTRAINT uc_users_user_email UNIQUE (user_email);

ALTER TABLE users
DROP CONSTRAINT IF EXISTS uc_users_username;
ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE favorites
DROP CONSTRAINT IF EXISTS fk_favorites_on_event;
ALTER TABLE favorites
    ADD CONSTRAINT fk_favorites_on_event FOREIGN KEY (event_id) REFERENCES events (id);

ALTER TABLE favorites
DROP CONSTRAINT IF EXISTS fk_favorites_on_user;
ALTER TABLE favorites
    ADD CONSTRAINT fk_favorites_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE viewed_user_events
DROP CONSTRAINT IF EXISTS fk_vieuseeve_on_event;
ALTER TABLE viewed_user_events
    ADD CONSTRAINT fk_vieuseeve_on_event FOREIGN KEY (event_id) REFERENCES events (id);

ALTER TABLE viewed_user_events
DROP CONSTRAINT IF EXISTS fk_vieuseeve_on_user;
ALTER TABLE viewed_user_events
    ADD CONSTRAINT fk_vieuseeve_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE event_tags
DROP CONSTRAINT IF EXISTS uc_event_tags_name;
ALTER TABLE event_tags
    ADD CONSTRAINT uc_event_tags_name UNIQUE (name);

CREATE TABLE IF NOT EXISTS unknown_user_interests
(
    event_type_id   BIGINT NOT NULL,
    unknown_user_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS unknown_users
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    bluetooth_id VARCHAR(100)                            NOT NULL,
    CONSTRAINT pk_unknown_users PRIMARY KEY (id)
);

ALTER TABLE unknown_users
DROP CONSTRAINT IF EXISTS uc_unknown_users_bluetooth;
ALTER TABLE unknown_users
    ADD CONSTRAINT uc_unknown_users_bluetooth UNIQUE (bluetooth_id);

ALTER TABLE unknown_user_interests
DROP CONSTRAINT IF EXISTS fk_unkuseint_on_event_type;
ALTER TABLE unknown_user_interests
    ADD CONSTRAINT fk_unkuseint_on_event_type FOREIGN KEY (event_type_id) REFERENCES event_types (id);

ALTER TABLE unknown_user_interests
DROP CONSTRAINT IF EXISTS fk_unkuseint_on_unknown_user;
ALTER TABLE unknown_user_interests
    ADD CONSTRAINT fk_unkuseint_on_unknown_user FOREIGN KEY (unknown_user_id) REFERENCES unknown_users (id);