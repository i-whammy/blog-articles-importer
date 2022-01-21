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

(defprotocol Register
  (execute [self company]))

(defmethod ig/init-key :blog-articles-importer/companies [_ companies]
  companies)