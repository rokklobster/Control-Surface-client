(ns control-surface-client.validation-test
  (:require [clojure.test :refer :all]
            [control-surface-client.validation :refer :all]
            [control-surface-client.models :refer [->TaskRunRequest]]))

(defn is-not [f] (is (not f)))

(deftest run-request-validation-test
  (testing "Fully valid request"
    (is (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" "2024-08-11T11:11:12+03:00" "test"))))
  (testing "Request with null scheduledAt"
    (is (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" nil "server"))))
  (testing "nil name"
    (is (not (valid-run-request? (->TaskRunRequest nil ["cmd"] "type" nil "server")))))
  (testing "empty name"
    (is (not (valid-run-request? (->TaskRunRequest "" ["cmd"] "type" nil "server")))))
  (testing "empty server"
    (is (not (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" nil "")))))
  (testing "nil server"
    (is-not (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" nil nil))))
  (testing "empty type"
    (is-not (valid-run-request? (->TaskRunRequest "name" ["cmd"] "" nil "server"))))
  (testing "nil type"
    (is-not (valid-run-request? (->TaskRunRequest "name" ["cmd"] nil nil "server"))))
  (testing "schedule is not a zoned dt"
    (is-not (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" "2024-08-11" "server"))))
  (testing "schedule is not a zoned dt"
    (is-not (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" "2024" "server"))))
  (testing "schedule is not a zoned dt"
    (is-not (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" "2024-08-11 12:23:23" "server"))))
  (testing "schedule is an invalid zoned dt"
    (is-not (valid-run-request? (->TaskRunRequest "name" ["cmd"] "type" "2024-08-11T12:12:43+64:45:01" "server"))))
  (testing "command contains non-str"
    (is-not (valid-run-request? (->TaskRunRequest "1" ["2" 2] "3" nil "4"))))
  (testing "command contains empty str"
    (is-not (valid-run-request? (->TaskRunRequest "1" ["2" ""] "3" nil "4"))))
  (testing "command contains nil"
    (is-not (valid-run-request? (->TaskRunRequest "1" ["2" nil] "3" nil "4"))))
  (testing "command is not a str[]"
    (is-not (valid-run-request? (->TaskRunRequest "1" "str" "3" nil "4"))))
  (testing "command is nil"
    (is-not (valid-run-request? (->TaskRunRequest "1" nil "3" nil "4")))))