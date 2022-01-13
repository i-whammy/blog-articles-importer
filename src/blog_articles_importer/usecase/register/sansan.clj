(ns blog-articles-importer.usecase.register.sansan
  (:require [blog-articles-importer.register :refer [Register]]
            [integrant.core :as ig]))

(def ^:private base-url "https://buildersbox.corp-sansan.com/rss")
(def ^:private company-name "株式会社Sansan")

(defn register* [{:keys [article-boundary]}])

(defrecord SansanRegister
  [options]
  Register
  (execute [options] (register* options)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/sansan [_ options]
  (map->SansanRegister options))