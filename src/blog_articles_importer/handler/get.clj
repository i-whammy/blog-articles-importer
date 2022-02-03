(ns blog-articles-importer.handler.get
  (:require [integrant.core :as ig]
            [ataraxy.response :as response]
            [blog-articles-importer.getter :refer [execute]]))

(defmethod ig/init-key :blog-articles-importer.handler/get [_ options]
  (let [company-getter (:get options)]
    (fn [{[_ company] :ataraxy/result}]
      (let [company-register (get-in options [:companies (keyword company)])]
        (if (nil? company-register)
          [::response/not-found "Invalid company name."]
          [::response/ok (execute company-getter company)])))))