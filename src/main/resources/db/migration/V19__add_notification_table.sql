
CREATE TABLE IF NOT EXISTS broadcasts
(
    id                          bigserial PRIMARY KEY,
    start_time                  TIMESTAMP WITH TIME ZONE    NOT NULL,
    repeat_time                 TIMESTAMP WITH TIME ZONE    NOT NULL,
    end_time                    TIMESTAMP WITH TIME ZONE    NOT NULL,
    status                      varchar(255)                NOT NULL,
    notification_strategy_name  varchar(255)                NOT NULL,
    message_strategy_name       varchar(255)                NOT NULL,
    notifications_created   boolean                         NOT NULL
);

CREATE TABLE IF NOT EXISTS notifications
(
    id              bigserial PRIMARY KEY,
    sent_date       TIMESTAMP WITH TIME ZONE,
    messageText     VARCHAR(255)                                         NOT NULL,
    status          VARCHAR(255)                                         NOT NULL,
    user_id         BIGINT REFERENCES users(id)                          NOT NULL,
    broadcast_id    BIGINT REFERENCES broadcasts(id) ON DELETE CASCADE   NOT NULL
);
