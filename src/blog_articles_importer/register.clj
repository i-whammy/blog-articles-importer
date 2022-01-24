(ns blog-articles-importer.register
  (:require [integrant.core :as ig]
            [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]))

(defn- get-articles-body [base-url]
  (-> (http/get base-url)
      :body))

(defn extract [base-url tags-set]
  (-> (get-articles-body base-url)
      (java.io.StringReader.)
      (java.io.BufferedReader.)
      (html/html-resource {:parser html/xml-parser})
      (html/select tags-set)))

(defn collect-registered-ids [returned-articles]
  {:registered-ids (map :id returned-articles)})

(defn ->iso-local-date
  [publish-date original-format]
   (.format (java.time.OffsetDateTime/parse publish-date original-format)
            (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE)))

(defn ->article-id
  [url short-name]
  (str short-name (clojure.string/replace url #"[^0-9]" "")))

(defn- ->article-vec [{:keys [id title publish-date url company-id]}]
  (conj []
        id
        title
        publish-date
        url
        company-id))

(defn ->articles-vec [articles formatter]
  (reduce
   (fn [acc article]
     (let [date-formatted-article (update article :publish-date #(->iso-local-date % formatter))]
       (conj acc (->article-vec date-formatted-article))))
   []
   articles))

(defprotocol Register
  (execute [self company]))

(defmethod ig/init-key :blog-articles-importer/companies [_ companies]
  companies)