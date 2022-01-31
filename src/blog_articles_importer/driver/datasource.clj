(ns blog-articles-importer.driver.datasource
  (:require [integrant.core :as ig]
            [hikari-cp.core :as hikari]))

(defmethod ig/init-key :blog-articles-importer.driver/datasource [_ options]
  (hikari/make-datasource options))

(defmethod ig/halt-key! :blog-articles-importer.driver/datasource [_ options]
  (hikari/close-datasource options))