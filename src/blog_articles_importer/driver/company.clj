(ns blog-articles-importer.driver.company
  (:require [blog-articles-importer.boundary.company :refer [CompanyBoundary]]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]
            [next.jdbc.connection :as connection]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs])
  (:import com.zaxxer.hikari.HikariDataSource))

(hugsql/def-sqlvec-fns "blog_articles_importer/db/sql/company.sql")
(declare get-company-sqlvec)

(def ^:private spec
  {:dbtype "postgres"
   :dbname "blog"
   :username "postgres"
   :password "password"
   :port-number 5432})

(defn- gen-connection []
  (connection/->pool HikariDataSource spec))

(defn- get-by* [_ short-name]
  (with-open [conn (gen-connection)]
    (jdbc/execute! conn (get-company-sqlvec {:short-name short-name}) {:builder-fn rs/as-unqualified-maps})))

(defrecord CompanyDriver
           [db]
  CompanyBoundary
  (get-by [db short-name]
    (get-by* db short-name)))

(defmethod ig/init-key :blog-articles-importer.driver/company [_ db]
  (map->CompanyDriver db))