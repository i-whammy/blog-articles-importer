CREATE DATABASE blog;
\c blog;
CREATE SCHEMA blog;
CREATE TABLE blog.article (
    id text PRIMARY KEY,
    title text,
    publish_date text,
    url text,
    company_id INTEGER
);
CREATE TABLE blog.company (
    id INTEGER PRIMARY KEY,
    name text,
    short_name text
);
INSERT INTO blog.company VALUES
(1, '株式会社ユーザベース', 'uzabase'),
(2, '株式会社Sansan', 'sansan');