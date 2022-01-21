(ns blog-articles-importer.usecase.register.sansan
  (:require [blog-articles-importer.register :refer [Register]]
            [integrant.core :as ig]
            [blog-articles-importer.boundary.article :as boundary]
            [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]))

(def ^:private base-url "https://buildersbox.corp-sansan.com/rss")
(def ^:private company-name "株式会社Sansan")

(defn- get-articles-body []
  (-> (http/get base-url)
      :body))

(defn- extract [body]
  (-> body
      (java.io.StringReader.)
      (html/html-resource {:parser html/xml-parser})
      (html/select #{[:item :title] [:item :link] [:item :pubDate]})))

(defn- transform [company tuple]
  (map (fn [[title link pubdate]]
         (let [url (first (:content link))]
           {:id (str company (clojure.string/replace link #"[^0-9]" ""))
            :title (first (:content title))
            :publish-date (first (:content pubdate))
            :url url
            :company-name company-name}))
       tuple))

(defn- ->articles-entity [company content]
  (->> (partition 3 content)
       (transform company)))

(defn- ->iso-publish-date [publish-date]
  (.format (java.time.OffsetDateTime/parse publish-date (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))
           (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE)))

(defn- ->article-vec [{:keys [id title publish-date url company-name]}]
  (conj []
        id
        title
        (->iso-publish-date publish-date)
        url
        company-name))

(defn- ->articles-vec [articles]
  (reduce
   (fn [acc article]
     (conj acc (->article-vec article)))
   []
   articles))

(defn- fetch [company]
  (->> (get-articles-body)
       (extract)
       (->articles-entity company)
       (->articles-vec)))

(defn- collect-registered-ids [returned-articles]
  {:registered-ids (map :id returned-articles)})

(defn register* [{:keys [article-boundary]} company]
  (->> (fetch company)
       (boundary/store article-boundary)
       (collect-registered-ids)))

(defrecord SansanRegister
  [options]
  Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/sansan [_ options]
  (map->SansanRegister options))