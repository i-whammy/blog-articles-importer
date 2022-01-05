(ns blog-articles-importer.handler.register
  (:require [integrant.core :as ig]
            [ataraxy.response :as response]
            [blog-articles-importer.usecase.uzabase :as uzabase]))

(defmethod ig/init-key :blog-articles-importer.handler/register [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok (uzabase/register options)]))