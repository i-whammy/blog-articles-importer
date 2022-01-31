(ns blog-articles-importer.driver.company
  (:require [blog-articles-importer.boundary.company :refer [CompanyBoundary]]
            [hugsql.core :as hugsql]
            [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]))

(hugsql/def-sqlvec-fns "blog_articles_importer/db/sql/company.sql")
(declare get-company-sqlvec)

(defn- get-by* [datasource short-name]
  (jdbc/execute! datasource (get-company-sqlvec {:short-name short-name}) {:builder-fn rs/as-unqualified-kebab-maps}))

(defrecord CompanyDriver
           [db]
  CompanyBoundary
  (get-by [{:keys [datasource]} short-name]
    (get-by* datasource short-name)))

(defmethod ig/init-key :blog-articles-importer.driver/company [_ options]
  (map->CompanyDriver options))