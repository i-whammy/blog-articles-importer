(ns blog-articles-importer.register 
  (:require [integrant.core :as ig]))

(defprotocol Register
  (execute [self]))

(defmethod ig/init-key :blog-articles-importer/companies [_ companies]
  companies)