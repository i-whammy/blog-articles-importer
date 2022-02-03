(ns blog-articles-importer.driver.article
  (:require [blog-articles-importer.boundary.article :refer [ArticleBoundary]]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(hugsql/def-sqlvec-fns "blog_articles_importer/db/sql/article.sql")
(declare store-articles-sqlvec get-articles-sqlvec)

(defn- store* [datasource articles]
  (jdbc/execute! datasource (store-articles-sqlvec {:articles articles})
                 {:return-keys true :builder-fn rs/as-unqualified-maps}))

(defn- get-by* [datasource company-short-name]
  (jdbc/execute! datasource (get-articles-sqlvec {:short-name company-short-name}) {:builder-fn rs/as-unqualified-maps}))

(defrecord ArticleDriver
           [options]
  ArticleBoundary
  (store [{:keys [datasource]} articles]
    (store* datasource articles))
  (get-by [{:keys [datasource]} company-short-name]
    (get-by* datasource company-short-name)))

(defmethod ig/init-key :blog-articles-importer.driver/article [_ options]
  (map->ArticleDriver options))