(ns control-surface-client.orchestrator-api
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]
            [control-surface-client.models :refer [->SelfLogApi]]
            [control-surface-client.util :refer [utc-now]]))

(defn http-post-json [url body cfg]
  (let [b (json/write-str body)]
    (client/post url
                 {:body b
                  :content-type :json
                  :headers {"X-CS-Token" (:userToken cfg)}})))

(defn push-log [items cfg]
  (let [base-url (:orchestratorUrl cfg)
        full-url (str base-url "/api/tasks/log")]
    (http-post-json full-url items cfg)))

(defn push-self-log [level message cfg]
  (let [r (->SelfLogApi (:ownName cfg) level (str (utc-now)) message)
        base-url (:orchestratorUrl cfg)
        full-url (str base-url "/api/logs")]
    (http-post-json full-url r cfg)))

(defn register-server [cfg ep-push ep-query ep-cancel force-update]
  (let [base-url (:orchestratorUrl cfg)
        full-url (str base-url "/api/servers")
        own-url (:ownUrl cfg)
        r {:serverName (:ownName cfg)
           :cancelUrl (str own-url ep-cancel)
           :pushUrl (str own-url ep-push)
           :queryUrl (str own-url ep-query)
           :forceUpdate force-update}]
    (http-post-json full-url r cfg)))

(defn unregister-server []
  nil)