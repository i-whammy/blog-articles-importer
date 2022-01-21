-- :name get-company :? :*
SELECT * FROM blog.articles
WHERE company_name = :company-name
ORDER BY publish_date DESC;