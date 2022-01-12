(ns blog-articles-importer.usecase.register.uzabase
  (:require [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]
            [blog-articles-importer.boundary.article :as boundary]
            [blog-articles-importer.register :refer [Register]]
            [integrant.core :as ig]))

(def ^:private base-url "https://tech.uzabase.com/feed/category/Blog")
(def ^:private company-name "株式会社ユーザベース")

(defn- get-articles-body []
  (-> (http/get base-url)
      :body))

(defn- extract [body]
  (-> body
      (html/html-snippet)
      (html/select #{[:entry :title] [:entry :published] [:entry :link]})))

(defn- transform [tuple]
  (map (fn [[title link pubdate _]]
         (let [url (get-in link [:attrs :href])]
           {:id (clojure.string/replace url #"[^0-9]" "")
            :title (first (:content title))
            :publish-date (first (:content pubdate))
            :url url
            :company-name company-name}))
       tuple))

(defn- ->articles-entity [content]
  (->> (partition 4 content)
       (transform)))

(defn- ->iso-publish-date [publish-date]
  (.format (java.time.OffsetDateTime/parse publish-date)
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

(defn- fetch []
  (->> (get-articles-body)
       (extract)
       (->articles-entity)
       (->articles-vec)))

(defn- collect-registered-ids [returned-articles]
  {:registered-ids (map :id returned-articles)})

(defn register* [{:keys [article-boundary]}]
  (->> (fetch)
       (boundary/store article-boundary)
       (collect-registered-ids)))

(defrecord UzabaseRegister
  [options]
  Register
  (execute [options] (register* options)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/uzabase [_ options]
  (map->UzabaseRegister options))