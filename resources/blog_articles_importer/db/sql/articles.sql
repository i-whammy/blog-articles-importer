-- :name store-articles :! :n
INSERT INTO blog.articles
VALUES :tuple*:articles
ON CONFLICT DO NOTHING;

-- :name get-articles :? :*
SELECT * FROM blog.articles
WHERE company_name = :company-name
ORDER BY publish_date