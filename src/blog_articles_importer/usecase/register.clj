(ns blog-articles-importer.usecase.register
  (:require [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]
            [blog-articles-importer.fetcher :as fetcher]
            [blog-articles-importer.register :as register]
            [integrant.core :as ig]))

(defn register* [{:keys [article-boundary company-boundary companies]} company-short-name]
  (let [company (-> (company-boundary/get-by company-boundary company-short-name)
                    first)
        fetcher (get companies (keyword company-short-name))]
    (->> (fetcher/execute fetcher company)
         (article-boundary/store article-boundary)
         (fetcher/collect-registered-ids))))

(defrecord CompanyRegister
           [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase/register [_ options]
  (map->CompanyRegister options))