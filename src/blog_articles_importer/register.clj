(ns blog-articles-importer.register
  (:require [integrant.core :as ig]
            [clj-http.client :as http]))

(defn get-articles-body [base-url]
  (-> (http/get base-url)
      :body))

;; [{:id "a"} {:id "b"}]
;; {:registered-ids ("a" "b")}
(defn collect-registered-ids [returned-articles]
  {:registered-ids (map :id returned-articles)})

(defn ->iso-local-date
  ([publish-date]
   (->iso-local-date publish-date (java.time.format.DateTimeFormatter/ISO_OFFSET_DATE_TIME)))
  ([publish-date original-format]
   (.format (java.time.OffsetDateTime/parse publish-date original-format)
            (java.time.format.DateTimeFormatter/ISO_LOCAL_DATE))))

(defprotocol Register
  (execute [self company]))

(defmethod ig/init-key :blog-articles-importer/companies [_ companies]
  companies)