(ns blog-articles-importer.usecase.register.m3
  (:require [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]
            [blog-articles-importer.register :as register]
            [integrant.core :as ig]
            [clojure.string :as s]))

(def ^:private base-url "https://www.m3tech.blog/feed")

(defn- ->article-id [url short-name]
  (let [slug (-> url
                 (s/replace "https://www.m3tech.blog/entry/" "")
                 (s/replace #"[/-]" ""))]
  (str short-name slug)))

(defn- transform [{:keys [id short-name]} tuple]
  (map (fn [[title link pubdate _]]
         (let [url (get-in link [:attrs :href])]
           {:id (->article-id url short-name)
            :title (first (:content title))
            :publish-date (first (:content pubdate))
            :url url
            :company-id id}))
       tuple))

(defn- ->articles-entity [company content]
  (->> (partition 4 content)
       (transform company)))

(defn- fetch [company]
  (let [articles-entity (->> (register/extract base-url #{[:entry :title] [:entry :link] [:entry :published]})
                             (->articles-entity company))]
    (register/->articles-vec
     articles-entity
     (java.time.format.DateTimeFormatter/ISO_OFFSET_DATE_TIME))))

(defn register* [{:keys [article-boundary company-boundary]} company-short-name]
  (let [company (-> (company-boundary/get-by company-boundary company-short-name)
                    first)]
    (->> (fetch company)
         (article-boundary/store article-boundary)
         (register/collect-registered-ids))))

(defrecord M3Register
           [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/m3 [_ options]
  (map->M3Register options))