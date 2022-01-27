(ns blog-articles-importer.register
  (:require [integrant.core :as ig]))

(defn- ->article-vec [{:keys [id title publish-date url company-id]}]
  (conj []
        id
        title
        publish-date
        url
        company-id))

(defn ->articles-vec [articles]
  (reduce
   (fn [acc article]
     (conj acc (->article-vec article)))
   []
   articles))

(defprotocol Register
  (execute [self company]))

(defmethod ig/init-key :blog-articles-importer/companies [_ companies]
  companies)