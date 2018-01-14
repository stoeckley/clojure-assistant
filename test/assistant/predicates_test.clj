(ns assistant.predicates-test
  (:require [clojure.test :refer :all]
            [assistant.predicates :refer :all]))

(deftest predicates
  (testing "bool?"
    (is (true? (bool? true)))
    (is (true? (bool? false)))
    (is (false? (bool? 4)))
    (is (false? (bool? nil)))
    (is (false? (bool? #())))
    (is (false? (bool? bool?))))
  (testing "count?"
    (is (true? (count? 0 [])))
    (is (true? (count? 1 [nil])))
    (is (true? (count? 1 [1])))
    (is (true? (count? 1 [""])))
    (is (true? (count? 0 "")))
    (is (true? (count? 1 "a")))
    (is (true? (count? 2 ":a")))
    (is (true? (count? 1 [[]])))
    (is (true? (count? 2 [[] []])))
    (is (true? (count? 2 [[] [:a]])))
    (is (true? (count? 2 [[] (range 1000)])))
    (is (true? (count? 2 [(range 1000) (range 1000)])))
    (is (true? (count? 1 (range 1))))
    (is (true? (count? 1000000 (range 1000000))))
    (is (true? (count? (- 3 2) ["hello there"])))
    (is (true? (count? 0 nil)))
    (is (false? (count? 0 [nil])))
    (is (false? (count? 0 :a)))
    (is (false? (count? 0 identity)))
    (is (false? (count? 3 (range 4))))
    (is (false? (count? 2 "foo"))))
  (testing "all?"
    (is (true? (all? 0 bool? [])))
    (is (true? (all? 0 bool? nil)))
    (is (true? (all? 0 string? [])))
    (is (true? (all? 0 string? nil)))
    (is (true? (all? 1 bool? [false])))
    (is (true? (all? 1 bool? [true])))
    (is (true? (all? 1 string? ["hi"])))
    (is (true? (all? 2 string? ["hi" "there"])))
    (is (true? (all? 1000 number? (range 1000))))
    (is (true? (all? 3 keyword? [:a :b :keyword])))
    (is (true? (all? 3 vector? (seq [[] [:a] [[[:b]]]]))))
    (is (true? (all? 2 #(all? 2 string? %) [["hi" "there"]
                                            ["foo" "bar"]])))
    (is (false? (all? 2 #(all? 2 string? %) [["hi" "there"]
                                             ["foo" "bar" "baz"]])))
    (is (false? (all? 0 bool? [ true])))
    (is (false? (all? 0 bool? true)))
    (is (false? (all? 0 string? ["hi"])))
    (is (false? (all? 0 string? "hi")))
    (is (false? (all? 1 bool? [:a])))
    (is (false? (all? 1 bool? ["hi"])))
    (is (false? (all? 1 string? [1])))
    (is (false? (all? 2 string? ["hi" :hi])))
    (is (false? (all? 1000 number? (range 1001))))
    (is (false? (all? 3 keyword? [:a :b :keyword :c]))))
  (testing "same-size? with 2 args"
    (is (true? (same-size? nil nil)))
    (is (true? (same-size? [] [])))
    (is (true? (same-size? "" "")))
    (is (true? (same-size? "hi" "yo")))
    (is (true? (same-size? [] nil)))
    (is (true? (same-size? [1] [:a])))
    (is (true? (same-size? (range 10) (range 10))))
    (is (true? (same-size? {:a 1 :b 2 :c 3} (range 3))))
    (is (true? (same-size? [1 2 3] '(4 5 6))))
    (is (false? (same-size? nil 1)))
    (is (false? (same-size? nil [1])))
    (is (false? (same-size? [] [[]])))
    (is (false? (same-size? "" "a")))
    (is (false? (same-size? [nil] nil)))
    (is (false? (same-size? [1] [1 :a])))
    (is (false? (same-size? (range 11) (range 10))))
    (is (false? (same-size? {:a 1 :b 2 :c 3} (range 2))))
    (is (false? (same-size? [1 2 3] '(4 5 6 7)))))
  (testing "same-size? with other than 2 args"
    (is (true? (same-size? nil nil nil)))
    (is (true? (same-size? nil)))
    (is (true? (same-size? "abc")))
    (is (true? (same-size? "abc" "def" "ghi")))
    (is (true? (same-size? [])))
    (is (true? (same-size? [] [] '() {})))
    (is (true? (same-size? [(range 2)] [(range 20)])))
    (is (true? (same-size? "" "" "" [])))
    (is (true? (same-size? [] nil [])))
    (is (true? (same-size? [1] [:a] "a" [[{} {} {:a 1}]])))
    (is (true? (same-size? (range 10) (range 10) (range 10) "abcdefghio")))
    (is (true? (same-size? {:a 1 :b 2 :c 3} (range 3) [1 2 3] {1 2 3 4 5 6})))
    (is (true? (same-size? [1 2 3] '(4 5 6) (range 3))))
    (is (false? (same-size? 1)))
    (is (false? (same-size? :a)))
    (is (false? (same-size? nil 1 2)))
    (is (false? (same-size? nil [1] [1])))
    (is (false? (same-size? [] [[]] [])))
    (is (false? (same-size? "" "a" "b")))
    (is (false? (same-size? [nil] nil nil)))
    (is (false? (same-size? [1] [1 :a] [1])))
    (is (false? (same-size? (range 11) (range 10) (range 10))))
    (is (false? (same-size? {:a 1 :b 2 :c 3} (range 2) (range 2))))
    (is (false? (same-size? [1 2 3] '(4 5 6 7) (range 3)))))
  (testing "map-structure?"
    (is (map-structure? integer? keyword? {1 :a 2 :b 3 :c 4 :d}))
    (is (false? (map-structure? integer? keyword? {1 :a 2 :b 3 :c 4 5})))
    (is (map-structure? integer? integer? (into {} (map vector (range 100000) (range 200000)))))
    (is (true? (map-structure? integer? integer? (assoc (into {} (map vector (range 100000) (range 200000))) -1 -2))))
    (is (false? (map-structure? integer? integer? (assoc (into {} (map vector (range 100000) (range 200000))) -1 :h))))
    (is (map-structure? integer? integer? (zipmap (range 22) (range 22))))
    (is (false? (map-structure? string? integer? (zipmap (range 22) (range 22)))))
    (is (map-structure? string? integer? {"hi" 4 "" 0}))
    (is (map-structure? string? integer? {}))
    (is (false? (map-structure? string? integer? {"hi" 4 "" 4.5})))))
