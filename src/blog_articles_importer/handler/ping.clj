(ns blog-articles-importer.handler.ping
  (:require [ataraxy.response :as response]
            [integrant.core :as ig]))

(defmethod ig/init-key :blog-articles-importer.handler/ping [_ _]
  (fn [{[_] :ataraxy/result}]
    [::response/ok "pong"]))