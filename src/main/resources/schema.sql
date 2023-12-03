DROP TABLE IF EXISTS post;

CREATE TABLE IF NOT EXISTS post (
    id INT NOT NULL,
    user_id INT NOT NULL,
    title varchar(250) NOT NULL,
    body TEXT,
    version int,
    PRIMARY KEY(id)
);