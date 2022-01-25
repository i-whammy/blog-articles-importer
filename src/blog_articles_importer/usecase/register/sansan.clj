(ns blog-articles-importer.usecase.register.sansan
  (:require [blog-articles-importer.register :as register]
            [integrant.core :as ig]
            [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]))

(def ^:private base-url "https://buildersbox.corp-sansan.com/rss")

(def ^:private fns {:id-fn (fn [url short-name] (str short-name (clojure.string/replace url #"[^0-9]" "")))
                    :url-fn (fn [link] (first (:content link)))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (first (:content publish-date)))})

(defn- fetch [company]
  (let [tags (register/extract base-url #{[:item :title] [:item :link] [:item :pubDate]})
        article-entity (register/->articles-entity {:tags tags
                                                    :partition-number 3
                                                    :fns fns
                                                    :company company})]
    (register/->articles-vec article-entity (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))))

(defn register* [{:keys [article-boundary company-boundary]} company-short-name]
  (let [company (-> (company-boundary/get-by company-boundary company-short-name)
                    first)]
    (->> (fetch company)
         (article-boundary/store article-boundary)
         (register/collect-registered-ids))))

(defrecord SansanRegister
           [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/sansan [_ options]
  (map->SansanRegister options))