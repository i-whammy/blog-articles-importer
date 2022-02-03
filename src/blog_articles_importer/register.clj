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
  (map ->article-vec articles))

(defprotocol Register
  (execute [self company]))

(defmethod ig/init-key :blog-articles-importer/companies [_ companies]
  companies)