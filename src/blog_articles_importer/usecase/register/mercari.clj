(ns blog-articles-importer.usecase.register.mercari
  (:require [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]
            [blog-articles-importer.register :as register]
            [integrant.core :as ig]
            [clojure.string :as s]))

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

(defn- fetch [company]
  (let [tags (register/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})
        articles-entity (register/->articles-entity {:tags tags
                                                     :partition-number 3
                                                     :fns fns
                                                     :company company})]
    (register/->articles-vec
     articles-entity
     (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))))

(defn register* [{:keys [article-boundary company-boundary]} company-short-name]
  (let [company (-> (company-boundary/get-by company-boundary company-short-name)
                    first)]
    (->> (fetch company)
         (article-boundary/store article-boundary)
         (register/collect-registered-ids))))

(defrecord MercariRegister
           [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/mercari [_ options]
  (map->MercariRegister options))