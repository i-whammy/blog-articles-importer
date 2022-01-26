(ns blog-articles-importer.handler.register
  (:require [integrant.core :as ig]
            [ataraxy.response :as response]
            [blog-articles-importer.register :as register]))

(defmethod ig/init-key :blog-articles-importer.handler/register [_ {:keys [register]}]
  (fn [{[_ company] :ataraxy/result}]
    (let [company-register (get-in register [:companies (keyword company)])]
      (if (nil? company-register)
        [::response/not-found "No register found."]
        [::response/ok (register/execute register company)]))))