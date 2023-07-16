create table if not exists parts_of_day
(
    id          BIGSERIAL  NOT NULL PRIMARY KEY,
    part_of_day int unique not null,
    name        varchar    NOT NULL
);

insert into parts_of_day(part_of_day, name)
values (0, 'MORNING'),
       (1, 'AFTERNOON'),
       (2, 'EVENING'),
       (3, 'NIGHT');
create table if not exists event_part_of_day
(
    event_id       BIGINT NOT NULL references events (id) ON DELETE CASCADE ON UPDATE CASCADE,
    part_of_day_id BIGINT NOT NULL references parts_of_day (id) ON DELETE CASCADE ON UPDATE CASCADE,
    primary key (event_id, part_of_day_id)
)