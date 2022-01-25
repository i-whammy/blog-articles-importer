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

(defn- transform [{:keys [id short-name]} tuple]
  (map (fn [[title link pub-date]]
         (let [url (first (:content link))]
           {:id (->article-id url short-name)
            :title (first (:content title))
            :publish-date (first (:content pub-date))
            :url url
            :company-id id}))
       tuple))

(defn- ->articles-entity [company content]
  (->> (partition 3 content)
       (transform company)))

(defn- fetch [company]
  (let [articles-entity (->> (register/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})
                             (->articles-entity company))]
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