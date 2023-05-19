CREATE TABLE IF NOT EXISTS unknown_users
(
    id           BIGSERIAL PRIMARY KEY,
    bluetooth_id varchar(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS registrant
(
    id                BIGSERIAL PRIMARY KEY,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    email             varchar(50),
    password          varchar(500)             not null,
    phone_number      varchar(20),
    status            varchar(255)             not null,
    username          varchar(50),
    verification_code integer
);

CREATE TABLE IF NOT EXISTS reset_codes
(
    id           BIGSERIAL PRIMARY KEY,
    email        varchar(100)                not null,
    is_enable    boolean                     not null,
    request_time timestamp(6) with time zone not null,
    reset_code   integer                     not null
);

CREATE TABLE IF NOT EXISTS users
(
    id                 BIGSERIAL PRIMARY KEY,
    birthday           DATE,
    bluetooth_id       varchar(100)             not null,
    created_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    enable_geolocation boolean                  not null,
    firebase_id        varchar(255)             not null,
    first_name         varchar(100),
    last_name          varchar(100),
    password           varchar(500)             not null,
    phone_number       varchar(255) UNIQUE,
    social_id          varchar(255) UNIQUE,
    user_avatar        varchar(200),
    user_email         varchar(50) UNIQUE       NOT NULL,
    username           varchar(100) UNIQUE
);

CREATE TABLE IF NOT EXISTS friends
(
    id                      BIGSERIAL PRIMARY KEY,
    added_to_block_at       TIMESTAMP,
    added_to_friends_at     TIMESTAMP,
    created_at              TIMESTAMP WITH TIME ZONE     NOT NULL,
    relationship_status     varchar(255)                 not null,
    user_who_get_follower   BIGINT REFERENCES users (id) NOT NULL,
    user_who_made_following BIGINT REFERENCES users (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_addresses
(
    id              BIGSERIAL PRIMARY KEY,
    building        varchar(10),
    city            varchar(100) not null,
    district        varchar(100),
    event_latitude  float(53)    not null,
    event_longitude float(53)    not null,
    event_position  geography(Point, 4326),
    floor           varchar(10),
    house_number    varchar(10),
    region          varchar(100),
    street          varchar(100)
);

CREATE TABLE IF NOT EXISTS event_types
(
    id                        BIGSERIAL PRIMARY KEY,
    dark_circle_animation     TEXT,
    dark_rectangle_animation  TEXT,
    light_circle_animation    TEXT,
    light_rectangle_animation TEXT,
    type_name                 varchar(50) UNIQUE NOT NULL,
    type_name_en              varchar(50) UNIQUE
);

CREATE TABLE IF NOT EXISTS unknown_user_interests
(
    unknown_user_id BIGINT REFERENCES unknown_users (id) NOT NULL,
    event_type_id   BIGINT REFERENCES event_types (id)   NOT NULL
);

CREATE TABLE IF NOT EXISTS user_interests
(
    user_id       BIGINT REFERENCES users (id)       NOT NULL,
    event_type_id BIGINT REFERENCES event_types (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                             BIGSERIAL PRIMARY KEY,
    created_at                     TIMESTAMP WITH TIME ZONE           NOT NULL,
    start_time                     TIMESTAMP WITH TIME ZONE           NOT NULL,
    end_time                       TIMESTAMP WITH TIME ZONE           NOT NULL,
    event_description              varchar(255),
    event_name                     varchar(100) UNIQUE                NOT NULL,
    event_status                   varchar(255)                       not null,
    is_eighteen_year_limit         boolean default false              not null,
    is_free                        boolean default false              not null,
    is_presence_of_alcohol         boolean default false              not null,
    is_private                     boolean                            not null,
    send_to_all_users_by_interests boolean                            not null,
    show_event_in_search           boolean                            not null,
    event_address_id               BIGINT REFERENCES event_addresses (id),
    event_type                     BIGINT REFERENCES event_types (id) NOT NULL,
    organizer_id                   BIGINT REFERENCES users (id)       NOT NULL
);

CREATE TABLE IF NOT EXISTS event_avatars
(
    id         BIGSERIAL PRIMARY KEY,
    avatar_url varchar(200),
    event_id   BIGINT REFERENCES events (id) NOT NULL
);

CREATE TABLE IF NOT EXISTS tags
(
    id       BIGSERIAL PRIMARY KEY,
    tag_name varchar(50) UNIQUE NOT NULL,
    tag_name_en varchar(50) UNIQUE
);

CREATE TABLE IF NOT EXISTS events_tags
(
    event_id BIGINT REFERENCES events (id) ON DELETE CASCADE NOT NULL,
    tag_id   BIGINT REFERENCES tags (id)                     NOT NULL
);

CREATE TABLE IF NOT EXISTS event_type_tags
(
    type_id BIGINT REFERENCES event_types (id) NOT NULL,
    tag_id  BIGINT REFERENCES tags (id)        NOT NULL
);

CREATE TABLE IF NOT EXISTS users_relations_with_events
(
    id                 BIGSERIAL PRIMARY KEY,
    is_favorite        boolean                       not null,
    is_invited         boolean                       not null,
    is_participant     boolean                       not null,
    is_viewed          boolean                       not null,
    is_want_to_go      boolean                       not null,
    event_relations_id BIGINT REFERENCES events (id) NOT NULL,
    user_relations_id  BIGINT REFERENCES users (id)  NOT NULL
);