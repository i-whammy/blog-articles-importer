-- :name store-articles :! :n
INSERT INTO blog.article
VALUES :tuple*:articles
ON CONFLICT DO NOTHING;

-- :name get-articles :? :*
SELECT * FROM blog.article
WHERE company_name = :company-name
ORDER BY publish_date DESC;