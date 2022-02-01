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
(2, 'Sansan株式会社', 'sansan'),
(3, 'エムスリー株式会社', 'm3'),
(4, '株式会社メルカリ', 'mercari'),
(5, '株式会社LINE', 'line'),
(6, '株式会社サイバーエージェント', 'cyberagent'),
(7, 'サイボウズ株式会社', 'cybozu'),
(8, '株式会社はてな', 'hatena'),
(9, '弁護士ドットコム株式会社', 'bengo4');