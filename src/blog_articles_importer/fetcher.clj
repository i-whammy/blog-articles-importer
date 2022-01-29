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

(defn- transform [tuple
                  ;; fns
                  {:keys [id-fn
                          url-fn
                          title-fn
                          publish-date-fn]}
                  ;; company
                  {:keys [id
                          short-name]}]
  (map (fn [[title link publish-date]]
         (let [url (url-fn link)]
           {:id (id-fn url short-name)
            :title (title-fn title)
            :publish-date (publish-date-fn publish-date)
            :url url
            :company-id id})) tuple))

(s/fdef ->articles-entity
        :ret seqable?)
(defn ->articles-entity [{:keys [tags partition-number fns company]}]
  (-> (partition partition-number tags)
      (transform fns company)))

(defprotocol Fetcher
  (execute [self company]))