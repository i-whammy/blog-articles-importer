-- :name get-company :? :*
SELECT * FROM blog.company
WHERE short_name = :short-name;