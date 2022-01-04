(ns blog-articles-importer.usecase.uzabase
  (:require [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [hugsql.core :as hugsql]
            [next.jdbc.result-set :as rs])
  (:import com.zaxxer.hikari.HikariDataSource))

(hugsql/def-sqlvec-fns "blog_articles_importer/db/sql/articles.sql")
(declare store-articles-sqlvec)
(declare get-articles-sqlvec)

(def ^:private base-url "https://tech.uzabase.com/feed/category/Blog")
(def ^:private company-name "株式会社ユーザベース")

(def ^:private spec
  {:dbtype "postgres"
   :dbname "blog"
   :username "postgres"
   :password "password"
   :port-number 5432})

(defn- gen-connection []
  (connection/->pool HikariDataSource spec))

(defn- get-articles-body []
  (-> (http/get base-url)
      :body))

(defn- extract [body]
  (-> body
      (html/html-snippet)
      (html/select #{[:entry :title] [:entry :published] [:entry :link]})))

(defn- transform [tuple]
  (map (fn [[title link pubdate _]]
         {:title (first (:content title))
          :publish-date (first (:content pubdate))
          :url (get-in link [:attrs :href])
          :company-name company-name})
       tuple))

(defn- ->articles-entity [content]
  (->> (partition 4 content)
       (transform)))

(defn fetch []
  (->> (get-articles-body)
       (extract)
       (->articles-entity)))

(defn- ->iso-publish-date [publish-date]
  (.format (java.time.OffsetDateTime/parse publish-date) (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE)))

(defn- ->article-vec [title publish-date url company-name]
  (conj []
        title
        (->iso-publish-date publish-date)
        url
        company-name))

(defn- ->articles-vec [articles]
  (reduce
   (fn [acc {:keys [title publish-date url company-name]}]
     (conj acc (->article-vec title publish-date url company-name)))
   []
   articles))

(defn store [articles]
  (let [articles-vec (->articles-vec articles)]
    (with-open [conn (gen-connection)]
      (jdbc/execute! conn (store-articles-sqlvec {:articles articles-vec})))))

(defn execute []
  (-> (fetch)
      (store)))

(defn- get* []
  (with-open [conn (gen-connection)]
    (jdbc/execute! conn (get-articles-sqlvec {:company-name company-name}) {:builder-fn rs/as-unqualified-maps})))

(defn get-articles []
  (get*))