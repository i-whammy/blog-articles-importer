(ns blog-articles-importer.handler.get
  (:require [integrant.core :as ig]
            [ataraxy.response :as response]
            [blog-articles-importer.getter :refer [execute]]))

(defmethod ig/init-key :blog-articles-importer.handler/get [_ options]
  (fn [{[_ company] :ataraxy/result}]
    (let [company-getter (get-in options [(keyword company) :getter])]
     [::response/ok (execute company-getter)])))