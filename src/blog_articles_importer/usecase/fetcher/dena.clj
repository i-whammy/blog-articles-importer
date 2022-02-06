(ns blog-articles-importer.usecase.fetcher.dena
  (:require [blog-articles-importer.fetcher :as fetcher]
            [clojure.string :as s]
            [integrant.core :as ig]))

(def ^:private base-url "https://engineering.dena.com/blog/index.xml")

(defn- ->article-id [url short-name]
  (let [slug (-> url
                 (s/replace #"https://engineering.dena.com/blog/" "")
                 (s/replace #"/" ""))]
  (str short-name slug)))

(def ^:private fns {:id-fn ->article-id
                    :url-fn (fn [link] (first (:content link)))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (-> (:content publish-date)
                                                            (first)
                                                            (fetcher/->iso-local-date (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))))})

(defn- fetch* [company]
  (let [tags (fetcher/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})]
    (fetcher/->articles-entity {:tags tags
                                :partition-number 3
                                :fns fns
                                :company company})))

(defrecord DenaFetcher
           [options]
  fetcher/Fetcher
  (execute [_ company] (fetch* company)))

(defmethod ig/init-key :blog-articles-importer.usecase.fetcher/dena [_ options]
  (map->DenaFetcher options))