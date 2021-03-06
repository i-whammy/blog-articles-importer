(ns blog-articles-importer.usecase.fetcher.uzabase
  (:require [blog-articles-importer.fetcher :as fetcher]
            [clojure.string :as s]
            [integrant.core :as ig]))

(def ^:private base-url "https://tech.uzabase.com/feed/category/Blog")

(def ^:private fns {:id-fn (fn [url short-name] (str short-name (s/replace url #"[^0-9]" "")))
                    :url-fn (fn [link] (get-in link [:attrs :href]))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (-> (:content publish-date)
                                                            (first)
                                                            (fetcher/->iso-local-date (java.time.format.DateTimeFormatter/ISO_OFFSET_DATE_TIME))))})

(defn- fetch* [company]
  (let [tags (fetcher/extract base-url #{[:entry :title] [:entry :link] [:entry :published]})]
    (fetcher/->articles-entity {:tags tags
                                :partition-number 4
                                :fns fns
                                :company company})))

(defrecord UzabaseFetcher
           [options]
  fetcher/Fetcher
  (execute [_ company] (fetch* company)))

(defmethod ig/init-key :blog-articles-importer.usecase.fetcher/uzabase [_ options]
  (map->UzabaseFetcher options))