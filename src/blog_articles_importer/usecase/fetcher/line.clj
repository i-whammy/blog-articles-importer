(ns blog-articles-importer.usecase.fetcher.line
  (:require [blog-articles-importer.fetcher :as fetcher]
            [clojure.string :as s]
            [integrant.core :as ig]))

(def ^:private base-url "https://engineering.linecorp.com/ja/feed/")

(def ^:private fns {:id-fn (fn [url short-name] (str short-name (-> url
                                                                    (s/replace "https://engineering.linecorp.com/ja/blog/" "")
                                                                    (s/replace "/" ""))))
                    :url-fn (fn [link] (first (:content link)))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (first (:content publish-date)))})

(defn- fetch* [company]
  (let [tags (fetcher/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})
        article-entity (fetcher/->articles-entity {:tags tags
                                                    :partition-number 3
                                                    :fns fns
                                                    :company company})]
    (fetcher/->articles-vec article-entity (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))))

(defrecord LineFetcher
           [options]
  fetcher/Fetcher
  (execute [_ company] (fetch* company)))

(defmethod ig/init-key :blog-articles-importer.usecase.fetcher/line [_ options]
  (map->LineFetcher options))