(ns blog-articles-importer.usecase.register.sansan
  (:require [blog-articles-importer.register :refer [Register]]
            [integrant.core :as ig]
            [blog-articles-importer.usecase.register.uzabase :as uzabase]))

(def ^:private base-url "https://buildersbox.corp-sansan.com/rss")
(def ^:private company-name "株式会社Sansan")

(defn register* [options]
  (uzabase/register* options))

(defrecord SansanRegister
  [options]
  Register
  (execute [options] (register* options)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/sansan [_ options]
  (map->SansanRegister options))