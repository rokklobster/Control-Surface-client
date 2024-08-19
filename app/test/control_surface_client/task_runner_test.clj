(ns control-surface-client.task-runner-test
  (:require [control-surface-client.task-runner :refer [build-args]]
            [control-surface-client.models :refer [map->Options]]
            [clojure.test :refer :all]))

(def config (map->Options
             {:rootSpaceCommands {"systemctl" "systemctl"
                                  "nginx" "nginx"}}))

(defn eq [a b] (is (= a b)))

(deftest task-runner-tests
  (testing "commands with shell type yields regular commands"
    (eq (build-args "shell" ["echo" "test"] config) ["echo" "test"]))
  (testing "shell commands should be specified explicitly"
    (eq (build-args "" ["test" "me"] config) nil))
  (testing "special commands are always sudo prefixed"
    (eq (build-args "nginx" ["-t"] config) ["sudo" "nginx" "-t"])))