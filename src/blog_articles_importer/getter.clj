(ns blog-articles-importer.getter)

(defprotocol Getter
  (execute [self company]))