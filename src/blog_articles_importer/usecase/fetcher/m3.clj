(ns blog-articles-importer.usecase.fetcher.m3
  (:require [blog-articles-importer.fetcher :as fetcher]
            [clojure.string :as s]
            [integrant.core :as ig]))

(def ^:private base-url "https://www.m3tech.blog/feed")

(defn- ->article-id [url short-name]
  (let [slug (-> url
                 (s/replace "https://www.m3tech.blog/entry/" "")
                 (s/replace #"[/-]" ""))]
  (str short-name slug)))

(def ^:private fns {:id-fn ->article-id
                    :url-fn (fn [link] (get-in link [:attrs :href]))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (first (:content publish-date)))})

(defn- fetch* [company]
  (let [tags (fetcher/extract base-url #{[:entry :title] [:entry :link] [:entry :published]})
        articles-entity (fetcher/->articles-entity {:tags tags
                                                     :partition-number 4
                                                     :fns fns
                                                     :company company})]

    (fetcher/->articles-vec
     articles-entity
     (java.time.format.DateTimeFormatter/ISO_OFFSET_DATE_TIME))))

(defrecord M3Fetcher
           [options]
  fetcher/Fetcher
  (execute [_ company] (fetch* company)))

(defmethod ig/init-key :blog-articles-importer.usecase.fetcher/m3 [_ options]
  (map->M3Fetcher options))