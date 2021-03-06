(ns blog-articles-importer.usecase.companies 
  (:require [blog-articles-importer.boundary.company :as company-boundary]))

(defn fetch [options]
  (let [company-boundary (:company-boundary options)
        companies (->> (company-boundary/get-all company-boundary)
                       (map :short-name))]
    {:companies companies}))