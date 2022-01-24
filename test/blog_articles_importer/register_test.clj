(ns blog-articles-importer.register-test
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

(t/deftest ->article-id
           (t/testing "generate article id with article url and company short name"
                      (t/is (= "uzabase20220121"
                               (sut/->article-id "https://tech.uzabase.com/entry/2022/01/21" "uzabase")))))