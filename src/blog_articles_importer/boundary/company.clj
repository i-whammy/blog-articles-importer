(ns blog-articles-importer.boundary.company)

(defprotocol CompanyBoundary
  (get-by [db short-name])
  (get-all [db]))