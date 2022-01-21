(ns blog-articles-importer.boundary.company)

(defprotocol CompanyBoundary
  (get [short-name]))