(ns blog-articles-importer.usecase.register.sansan
  (:require [blog-articles-importer.register :as register]
            [integrant.core :as ig]
            [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]
            [net.cgrand.enlive-html :as html]))

(def ^:private base-url "https://buildersbox.corp-sansan.com/rss")

(defn- extract [body]
  (-> body
      (java.io.StringReader.)
      (html/html-resource {:parser html/xml-parser})
      (html/select #{[:item :title] [:item :link] [:item :pubDate]})))

(defn- transform [{:keys [id short-name]} tuple]
  (map (fn [[title link pubdate]]
         (let [url (first (:content link))]
           {:id (str short-name (clojure.string/replace link #"[^0-9]" ""))
            :title (first (:content title))
            :publish-date (first (:content pubdate))
            :url url
            :company-id id}))
       tuple))

(defn- ->articles-entity [company content]
  (->> (partition 3 content)
       (transform company)))

(defn- ->iso-publish-date [publish-date]
  (.format (java.time.OffsetDateTime/parse publish-date (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))
           (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE)))

(defn- ->article-vec [{:keys [id title publish-date url company-id]}]
  (conj []
        id
        title
        (->iso-publish-date publish-date)
        url
        company-id))

(defn- ->articles-vec [articles]
  (reduce
   (fn [acc article]
     (conj acc (->article-vec article)))
   []
   articles))

(defn- fetch [company]
  (->> (register/get-articles-body base-url)
       (extract)
       (->articles-entity company)
       (->articles-vec)))

(defn register* [{:keys [article-boundary company-boundary]} company-short-name]
(let [company (-> (company-boundary/get-by company-boundary company-short-name)
                  first)]
  (->> (fetch company)
       (article-boundary/store article-boundary)
       (register/collect-registered-ids))
  ))

(defrecord SansanRegister
  [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/sansan [_ options]
  (map->SansanRegister options))