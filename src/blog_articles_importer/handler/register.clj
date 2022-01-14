(ns blog-articles-importer.handler.register
  (:require [integrant.core :as ig]
            [ataraxy.response :as response]
            [blog-articles-importer.register :refer [execute]]))

(defmethod ig/init-key :blog-articles-importer.handler/register [_ options]
  (fn [{[_ company] :ataraxy/result}]
    (let [company-register (get-in options [(keyword company) :register])]
      (if (nil? company-register)
        [::response/internal-server-error "No register found."]
        [::response/ok (execute company-register)]))))