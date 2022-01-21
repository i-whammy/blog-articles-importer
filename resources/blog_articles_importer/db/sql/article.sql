-- :name store-articles :! :n
INSERT INTO blog.article
VALUES :tuple*:articles
ON CONFLICT DO NOTHING;

-- :name get-articles :? :*
SELECT a.id, a.title, a.publish_date, a.url, c.name FROM blog.article a
INNER JOIN blog.company c
ON a.company_id = c.id
WHERE c.short_name = :short-name
ORDER BY publish_date DESC;