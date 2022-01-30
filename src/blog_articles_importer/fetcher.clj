(ns blog-articles-importer.fetcher
  (:require [clj-http.client :as http]
            [net.cgrand.enlive-html :as html]
            [clojure.spec.alpha :as s]))

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

(s/def :blog-articles-importer/iso-local-date #(re-matches #"[0-9]{4}-[0-9]{2}-[0-9]{2}" %))
(s/def :blog-articles-importer.article/id string?)
(s/def :blog-articles-importer.article/title string?)
(s/def :blog-articles-importer.article/publish-date (s/and string?
                                                           :blog-articles-importer/iso-local-date))
(s/def :blog-articles-importer.article/url string?)
(s/def :blog-articles-importer.article/company-id int?)
(s/def :blog-articles-importer/article (s/and #(s/valid? :blog-articles-importer.article/id (:id %))
                                              #(s/valid? :blog-articles-importer.article/title (:title %))
                                              #(s/valid? :blog-articles-importer.article/publish-date (:publish-date %))
                                              #(s/valid? :blog-articles-importer.article/url (:url %))
                                              #(s/valid? :blog-articles-importer.article/company-id (:company-id %))))
(s/def :blog-articles-importer/articles (s/coll-of #(s/valid? :blog-articles-importer/article %)))
(defn- transform [tuple
                  ;; fns
                  {:keys [id-fn
                          url-fn
                          title-fn
                          publish-date-fn]}
                  ;; company
                  {:keys [id
                          short-name]}]
  {:pre [(s/valid? fn? id-fn)
         (s/valid? fn? url-fn)
         (s/valid? fn? title-fn)
         (s/valid? fn? publish-date-fn)
         (s/valid? int? id)
         (s/valid? string? short-name)]
   :post [(s/valid? :blog-articles-importer/articles %)]}
  (map (fn [[title link publish-date]]
         (let [url (url-fn link)]
           {:id (id-fn url short-name)
            :title (title-fn title)
            :publish-date (publish-date-fn publish-date)
            :url url
            :company-id id})) tuple))

(defn ->articles-entity [{:keys [tags partition-number fns company]}]
  {:pre [(s/valid? int? partition-number)
         (s/valid? map? fns)]}
  (-> (partition partition-number tags)
      (transform fns company)))

(defprotocol Fetcher
  (execute [self company]))