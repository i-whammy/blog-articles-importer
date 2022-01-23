(ns blog-articles-importer.register
  (:require [clojure.test :as t]
            [blog-articles-importer.register :as sut]))

(t/deftest collect-registered-ids
           (t/testing "return a collection of ids with registered-ids key"
                      (t/is (= {:registered-ids '("a" "b")}
                               (sut/collect-registered-ids [{:id "a" :title "article title a"}
                                                            {:id "b" :title "article title b"}])))))

(t/deftest ->iso-local-date
           (t/testing "return a string value of iso local date with given formatter"
                      (t/is (= "2022-01-22"
                               (sut/->iso-local-date "Sat, 22 Jan 2022 09:00:00 +0900"
                                                     (java.time.format.DateTimeFormatter/RFC_1123_DATE_TIME))))))