ALTER TABLE event_types
    ADD dark_circle_animation_path VARCHAR(50);

ALTER TABLE event_types
    ADD dark_rectangle_animation_path VARCHAR(50);

ALTER TABLE event_types
    ADD light_circle_animation_path VARCHAR(50);

ALTER TABLE event_types
    ADD light_rectangle_animation_path VARCHAR(50);

ALTER TABLE event_types
    DROP COLUMN dark_circle_animation;

ALTER TABLE event_types
    DROP COLUMN dark_rectangle_animation;

ALTER TABLE event_types
    DROP COLUMN light_circle_animation;

ALTER TABLE event_types
    DROP COLUMN light_rectangle_animation;