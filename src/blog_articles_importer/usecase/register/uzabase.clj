(ns blog-articles-importer.usecase.register.uzabase
  (:require [blog-articles-importer.boundary.article :as article-boundary]
            [blog-articles-importer.boundary.company :as company-boundary]
            [blog-articles-importer.register :as register]
            [integrant.core :as ig]))

(def ^:private base-url "https://tech.uzabase.com/feed/category/Blog")

(def ^:private fns {:id-fn (fn [url short-name] (str short-name (clojure.string/replace url #"[^0-9]" "")))
                    :url-fn (fn [link] (get-in link [:attrs :href]))
                    :title-fn (fn [title] (first (:content title)))
                    :publish-date-fn (fn [publish-date] (first (:content publish-date)))})

(defn- fetch [company]
  (let [tags (register/extract base-url #{[:entry :title] [:entry :link] [:entry :published]})
        articles-entity (register/->articles-entity {:tags tags
                                                     :partition-number 4
                                                     :fns fns
                                                     :company company})]
    (register/->articles-vec
     articles-entity
     (java.time.format.DateTimeFormatter/ISO_OFFSET_DATE_TIME))))

(defn register* [{:keys [article-boundary company-boundary]} company-short-name]
  (let [company (-> (company-boundary/get-by company-boundary company-short-name)
                    first)]
    (->> (fetch company)
         (article-boundary/store article-boundary)
         (register/collect-registered-ids))))

(defrecord UzabaseRegister
           [options]
  register/Register
  (execute [options company] (register* options company)))

(defmethod ig/init-key :blog-articles-importer.usecase.register/uzabase [_ options]
  (map->UzabaseRegister options))