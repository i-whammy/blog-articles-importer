(ns blog-articles-importer.usecase.register.uzabase
  (:require [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]
            [blog-articles-importer.register :as register]
            [integrant.core :as ig]))

(def ^:private base-url "https://tech.uzabase.com/feed/category/Blog")

(defn- transform [{:keys [id short-name]} tuple]
  (map (fn [[title link pubdate _]]
         (let [url (get-in link [:attrs :href])]
           {:id (register/->article-id url short-name)
            :title (first (:content title))
            :publish-date (first (:content pubdate))
            :url url
            :company-id id}))
       tuple))

(defn- ->articles-entity [company content]
  (->> (partition 4 content)
       (transform company)))

(defn- ->article-vec [{:keys [id title publish-date url company-id]}]
  (conj []
        id
        title
        (register/->iso-local-date publish-date (java.time.format.DateTimeFormatter/ISO_OFFSET_DATE_TIME))
        url
        company-id))

(defn- ->articles-vec [articles]
  (reduce
   (fn [acc article]
     (conj acc (->article-vec article)))
   []
   articles))

(defn- fetch [company]
  (->> (register/extract base-url #{[:entry :title] [:entry :link] [:entry :published]})
       (->articles-entity company)
       (->articles-vec)))

(defn register* [{:keys [article-boundary company-boundary]} company-short-name]
  (let [company (-> (company-boundary/get-by company-boundary company-short-name)
                    first)]
    (->> (fetch company)
         (article-boundary/store article-boundary)
         (register/collect-registered-ids))))

(defrecord UzabaseRegister
  [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/uzabase [_ options]
  (map->UzabaseRegister options))