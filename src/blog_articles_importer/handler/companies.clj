(ns blog-articles-importer.handler.companies 
  (:require [integrant.core :as ig]
            [ataraxy.response :as response]
            [blog-articles-importer.usecase.companies :as companies]))

(defmethod ig/init-key :blog-articles-importer.handler/companies [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (companies/fetch options)]))