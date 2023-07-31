delete from users_relations_with_events;
delete from event_avatars;
delete from events;
delete from user_interests;
delete from users;
delete from event_types where id in (1000, 1001);
delete from event_addresses;
