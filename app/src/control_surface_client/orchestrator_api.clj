(ns control-surface-client.orchestrator-api
  (:require [control-surface-client.models :refer [->TaskOutput
                                                   ->SelfLogApi]]
            [control-surface-client.util :refer [utc-now]]))

(defn http-post-json [url body] nil) ;; todo: impl

(defn push-log [name text cfg]
  (let [r (->TaskOutput name (str (utc-now)) text)]
    (http-post-json (str (:orchestratorUrl cfg) "/api/tasks/log") r)))

(defn push-self-log [level message cfg]
  (let [r (->SelfLogApi (:ownName cfg) level (str (utc-now)) message)]
    (http-post-json (str (:orchestratorUrl cfg) "/api/logs") r)))