(ns blog-articles-importer.driver.article
  (:require [blog-articles-importer.boundary.article :refer [ArticleBoundary]]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]
            [next.jdbc.connection :as connection]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs])
  (:import com.zaxxer.hikari.HikariDataSource))

(hugsql/def-sqlvec-fns "blog_articles_importer/db/sql/articles.sql")
(declare store-articles-sqlvec)
(declare get-articles-sqlvec)

(def ^:private spec
  {:dbtype "postgres"
   :dbname "blog"
   :username "postgres"
   :password "password"
   :port-number 5432})

(defn- gen-connection []
  (connection/->pool HikariDataSource spec))

(defn- store* [_ articles]
  (with-open [conn (gen-connection)]
    (jdbc/execute! conn (store-articles-sqlvec {:articles articles})
                   {:return-keys true :builder-fn rs/as-unqualified-maps})))

(defn- get-by* [_ company-name]
  (with-open [conn (gen-connection)]
    (jdbc/execute! conn (get-articles-sqlvec {:company-name company-name}) {:builder-fn rs/as-unqualified-maps})))

(defrecord ArticleDriver
           [db]
  ArticleBoundary
  (store [db articles]
    (store* db articles))
  (get-by [db company-name]
    (get-by* db company-name)))

(defmethod ig/init-key :blog-articles-importer.driver/article [_ db]
  (map->ArticleDriver db))