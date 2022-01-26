(ns blog-articles-importer.usecase.fetcher.sansan
  (:require [blog-articles-importer.fetcher :as fetcher]
            [clojure.string :as s]
            [integrant.core :as ig]))

(def ^:private base-url "https://buildersbox.corp-sansan.com/rss")

(def ^:private fns {:id-fn (fn [url short-name] (str short-name (s/replace url #"[^0-9]" "")))
                    :url-fn (fn [link] (first (:content link)))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (first (:content publish-date)))})

(defn- fetch* [company]
  (let [tags (fetcher/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})
        articles-entity (fetcher/->articles-entity {:tags tags
                                                    :partition-number 3
                                                    :fns fns
                                                    :company company})]
    (fetcher/->articles-vec articles-entity (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))))

(defrecord SansanFetcher
           [options]
  fetcher/Fetcher
  (execute [_ company] (fetch* company)))

(defmethod ig/init-key :blog-articles-importer.usecase.fetcher/sansan [_ options]
  (map->SansanFetcher options))