(ns control-surface-client.validation
  (:require [java-time.api :as jt]))

(defn valid-config? [cfg]
  (and
   (some? (:ownName cfg))
   (some? (:orchestratorUrl cfg))
   (some? (:ownUrl cfg))
   (or (not (:usePull cfg)) (> (:pullInterval cfg) 0))))

(defn is-zoned-dt-string? [s]
  (try (some? (jt/zoned-date-time s))
       (catch Exception _ false)))

(defn valid-run-request? [req]
  (let [sched (:scheduledAt req)]
    (and
     (some? (:taskName req))
     (some? (:taskCommand req))
     (or (nil? sched) (is-zoned-dt-string? sched))
     (some? (:targetServer req)))))