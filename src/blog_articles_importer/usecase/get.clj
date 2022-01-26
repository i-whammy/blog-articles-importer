(ns blog-articles-importer.usecase.get
  (:require [blog-articles-importer.getter :refer [Getter]]
            [blog-articles-importer.boundary.article :as article-boundary]
            [integrant.core :as ig]
            [blog-articles-importer.boundary.company :as company-boundary]
            [clojure.set :as set]))

(defn get-articles [{:keys [article-boundary company-boundary]} company-short-name]
  (let [company (first (company-boundary/get-by company-boundary company-short-name))
        articles (article-boundary/get-by article-boundary (:short-name company))]
    (map #(set/rename-keys % {:name :company-name}) articles)))

(defrecord UzabaseGetter [options]
  Getter
  (execute [options company] (get-articles options company)))

(defmethod ig/init-key :blog-articles-importer.usecase/get [_ options]
  (map->UzabaseGetter options))
