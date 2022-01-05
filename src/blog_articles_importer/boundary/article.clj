(ns blog-articles-importer.boundary.article)

(defprotocol ArticleBoundary
  (store [db articles])
  (get-by [db company-name]))