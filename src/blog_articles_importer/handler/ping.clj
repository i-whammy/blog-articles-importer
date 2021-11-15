(ns blog-articles-importer.handler.ping
  (:require [ataraxy.core :as ataraxy]
            [ataraxy.response :as response]
            [integrant.core :as ig]))

(defmethod ig/init-key :blog-articles-importer.handler/ping [_ options]
  (fn [{[_] :ataraxy/result}]
    [::response/ok "pong"]))