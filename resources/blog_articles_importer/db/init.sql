CREATE DATABASE blog;
\c blog;
CREATE SCHEMA blog;
CREATE TABLE blog.articles (
    title text,
    publish_date text,
    url text,
    company_name text
);