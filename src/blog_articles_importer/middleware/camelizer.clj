(ns blog-articles-importer.middleware.camelizer
  (:require [integrant.core :as ig]
            [camel-snake-kebab.core :as csk]
            [clojure.set :refer [rename-keys]]))

(defn- camelize-keys-in-map [m]
  (rename-keys m
               (zipmap (keys m) (map csk/->camelCase (keys m)))))

(defn camelize-keys [col]
  (cond
    (map? col) (camelize-keys-in-map col)
    (vector? col) (vec (map camelize-keys col))
    (seq? col) (lazy-seq (map camelize-keys  col))
    :else col))

(defn wrap-camelize [handler]
  (fn [req]
    (-> (handler req)
        (camelize-keys))))

(defmethod ig/init-key :blog-articles-importer.middleware/camelizer [_ _]
  wrap-camelize)