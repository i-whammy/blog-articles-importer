(ns blog-articles-importer.usecase.fetcher.mercari
  (:require [blog-articles-importer.fetcher :as fetcher]
            [clojure.string :as s]
            [integrant.core :as ig]))

(def ^:private base-url "https://engineering.mercari.com/blog/feed.xml/")

(defn- ->article-id [url short-name]
  (let [slug (-> url
                 (s/replace "https://engineering.mercari.com/blog/entry/" "")
                 (s/replace #"[/-]" ""))]
    (str short-name slug)))

(def ^:private fns {:id-fn ->article-id
                    :url-fn (fn [link] (first (:content link)))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (first (:content publish-date)))})

(defn- fetch* [company]
  (let [tags (fetcher/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})
        articles-entity (fetcher/->articles-entity {:tags tags
                                                    :partition-number 3
                                                    :fns fns
                                                    :company company})]
    (fetcher/->articles-vec
     articles-entity
     (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))))

(defrecord MercariFetcher
           [options]
  fetcher/Fetcher
  (execute [_ company] (fetch* company)))

(defmethod ig/init-key :blog-articles-importer.usecase.fetcher/mercari [_ options]
  (map->MercariFetcher options))