(ns blog-articles-importer.middleware.camelizer-test
  (:require [clojure.test :as t]
            [blog-articles-importer.middleware.camelizer :as sut]))

(t/deftest camelize-keys
           (t/testing "camelize a key in a map"
                      (t/is (= (sut/camelize-keys {:a-b "a"})
                               {:aB "a"})))
           (t/testing "camelize keys in a map"
                      (t/is (= (sut/camelize-keys {:a-b "a" "b-c" "b"})
                               {:aB "a" "bC" "b"})))
           (t/testing "camelize keys in map in a vector"
                      (t/is (= (sut/camelize-keys [{:a-b "a" :b-c "b"}])
                               [{:aB "a" :bC "b"}])))
           (t/testing "camelize keys in map in a lazy seq"
             (t/is (= (sut/camelize-keys '({:a-b "a" :b-c "b"}))
                      '({:aB "a" :bC "b"}))))
           (t/testing "camelize keys in map in a nested vector"
             (t/is (= (sut/camelize-keys [[{:a-b "a" :b-c "b"}]])
                      [[{:aB "a" :bC "b"}]])))
           (t/testing "let others left in a vector"
             (t/is (= (sut/camelize-keys [[{:a-b "a" :b-c "b"}] :abc])
                      [[{:aB "a" :bC "b"}] :abc]))))