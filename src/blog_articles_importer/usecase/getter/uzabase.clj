(ns blog-articles-importer.usecase.getter.uzabase
  (:require [blog-articles-importer.getter :refer [Getter]]
            [blog-articles-importer.boundary.article :as boundary]
            [integrant.core :as ig]))

(def ^:private company-name "株式会社ユーザベース")

(defn get-articles [{:keys [article-boundary]}]
  (boundary/get-by article-boundary company-name))

(defrecord UzabaseGetter [options]
  Getter
  (execute [options] (get-articles options)))

(defmethod ig/init-key :blog-articles-importer.usecase.getter/uzabase [_ options]
  (map->UzabaseGetter options))