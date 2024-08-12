(ns control-surface-client.validation
  (:require [java-time.api :as jt]
            [clojure.string :refer [blank?]]))

(defn valid-config? [cfg]
  (and
   (some? (:ownName cfg))
   (some? (:orchestratorUrl cfg))
   (some? (:ownUrl cfg))
   (some? (:port cfg))
   (> (:port cfg) 0)
   (or (not (:usePull cfg)) (> (:pullInterval cfg) 0))))

(defn is-zoned-dt-string? [s]
  (try (some? (jt/zoned-date-time s))
       (catch Exception _ false)))

(defn valid-run-request? [req]
  (let [sched (:scheduledAt req)]
    (and
     (not (blank? (:taskName req)))
     (not (blank? (:taskType req)))
     (some? (:taskCommand req))
     (instance? java.util.Collection (:taskCommand req))
     (every? (partial instance? String) (:taskCommand req))
     (not (some (partial re-matches #"(\W|^)su(do)?(\W|$)") (:taskCommand req)))
     (not (some blank? (:taskCommand req)))
     (or (blank? sched) (is-zoned-dt-string? sched))
     (not (blank? (:targetServer req))))))