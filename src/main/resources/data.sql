INSERT INTO event_types (type_name)
SELECT v.type_name
FROM (VALUES ('sport'), ('dance'), ('party')) AS v(type_name)
WHERE
    NOT EXISTS(SELECT id FROM event_types
                         WHERE type_name = v.type_name);

INSERT INTO tags (tag_name)
SELECT t.tag_name
FROM (VALUES ('rock'), ('cycling')) AS t(tag_name)
WHERE
    NOT EXISTS(SELECT id FROM tags
               WHERE tag_name = t.tag_name);