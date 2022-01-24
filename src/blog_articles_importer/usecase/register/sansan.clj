(ns blog-articles-importer.usecase.register.sansan
  (:require [blog-articles-importer.register :as register]
            [integrant.core :as ig]
            [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]))

(def ^:private base-url "https://buildersbox.corp-sansan.com/rss")

(defn- transform [{:keys [id short-name]} tuple]
  (map (fn [[title link pubdate]]
         (let [url (first (:content link))]
           {:id (register/->article-id url short-name)
            :title (first (:content title))
            :publish-date (first (:content pubdate))
            :url url
            :company-id id}))
       tuple))

(defn- ->articles-entity [company content]
  (->> (partition 3 content)
       (transform company)))

(defn- ->article-vec [{:keys [id title publish-date url company-id]}]
  (conj []
        id
        title
        (register/->iso-local-date publish-date (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))
        url
        company-id))

(defn- ->articles-vec [articles]
  (reduce
   (fn [acc article]
     (conj acc (->article-vec article)))
   []
   articles))

(defn- fetch [company]
  (->> (register/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})
       (->articles-entity company)
       (->articles-vec)))

(defn register* [{:keys [article-boundary company-boundary]} company-short-name]
(let [company (-> (company-boundary/get-by company-boundary company-short-name)
                  first)]
  (->> (fetch company)
       (article-boundary/store article-boundary)
       (register/collect-registered-ids))))

(defrecord SansanRegister
  [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/sansan [_ options]
  (map->SansanRegister options))