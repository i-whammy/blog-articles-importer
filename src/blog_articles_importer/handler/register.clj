(ns blog-articles-importer.handler.register
  (:require [integrant.core :as ig]
            [ataraxy.response :as response]
            [blog-articles-importer.register :refer [execute]]))

(defmethod ig/init-key :blog-articles-importer.handler/register [_ options]
  (fn [{[_ company] :ataraxy/result}]
    (let [company-register (get options (keyword company))]
    [::response/ok (execute company-register)])))