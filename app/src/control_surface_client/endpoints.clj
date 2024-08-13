(ns control-surface-client.endpoints
  (:require [clojure.pprint :as pprint]
            [control-surface-client.models :refer [map->TaskCancelRequest
                                                   map->TaskRunRequest]]
            [control-surface-client.task-runner :as tr]
            [control-surface-client.util :refer [api-response]]
            [control-surface-client.validation :refer [valid-run-request?]]
            [java-time.api :as jt]))

(defn not-found []
  {:status 404
   :body "Not found."
   :headers {"Content-Type" "text/plain"}})

(defn ep-echo [req]
  {:status 200
   :body (with-out-str (pprint/pprint req))
   :headers {"Content-Type" "text/plain"}})

(defn ep-receive-push [req cf]
  (let [push (map->TaskRunRequest (:params req))]
    (cond
      (-> push valid-run-request? not) (api-response 400 false "Invalid request")
      (:usePull cf) (api-response 400 false "Pushes are disabled")
      (not= (:ownName cf) (:targetServer push)) (api-response 400 false "Wrong target")
      (jt/after?
       (jt/zoned-date-time (:scheduledAt push))
       (jt/plus (jt/zoned-date-time) (jt/seconds 1))) (api-response 400 false "For pushes, task is executed if and only if scheduled time is not in future")
      :else
      (if
        (tr/run-task (:taskName push) (:taskType push) (:taskCommand push) cf)
        (api-response 200 true (str "running " (:taskType push) " | " (:taskCommand push)))
        (api-response 500 false "Failed to run task")))))

(defn ep-cancel-task [req cf]
  (let [r (map->TaskCancelRequest (:params req))]
    (cond
      (:usePull cf) (api-response 400 false "Pushes are disabled")
      (not= (:ownName cf) (:targetServer r)) (api-response 400 false "Wrong target")
      :else 
      (if (tr/cancel-task (:taskName r)) 
        (api-response 200 true (str "Task " (:taskName r) " was cancelled"))
        (api-response 500 false "Failed to cancel task")))))

(defn ep-query-tasks []
  (api-response 200 true "OK" (tr/query-tasks)))