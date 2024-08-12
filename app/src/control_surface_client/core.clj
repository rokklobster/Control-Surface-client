(ns control-surface-client.core
  (:require [clojure.edn]
            [clojure.pprint :as pprint]
            [clojure.tools.logging :as log]
            [compojure.core :as comp]
            [compojure.route :as route]
            [control-surface-client.db :refer [ensure-db]]
            [control-surface-client.endpoints :refer [ep-echo
                                                      ep-receive-push
                                                      ep-cancel-task
                                                      ep-query-tasks
                                                      not-found]]
            [control-surface-client.models :refer [map->Options]]
            [control-surface-client.validation :refer [valid-config?]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]))

(defonce server (atom nil))
(defonce cfg (atom nil))

(comp/defroutes routes
  (comp/ANY "/echo" req (ep-echo req))
  (comp/POST "/push" req (ep-receive-push req @cfg))
  (comp/POST "/cancel" req (ep-cancel-task req @cfg))
  (comp/GET "/cancel" req (ep-cancel-task req @cfg))
  (comp/GET "/query" _ (ep-query-tasks))
  (route/not-found (not-found)))

(def app
  (-> routes
      wrap-keyword-params
      wrap-json-params
      wrap-params))

(defn start-server [_]
  (reset! server
          (jetty/run-jetty
           (fn [req] (log/info "request: " (:uri req)) (app req))
           {:port 30011
            :join? false})))

(defn stop-server []
  (when-some [s @server]
    (.stop s)
    ;; (.close s)
    (reset! server nil)))

(defn restart-server []
  (stop-server)
  (start-server nil))

(log/info "running from " (System/getProperty "user.dir"))
(reset! cfg (map->Options (clojure.edn/read-string (slurp "./config"))))
(log/info "read config: " (with-out-str (pprint/pprint cfg)))

(cond
  (valid-config? @cfg) (-> nil ensure-db start-server)
  :else (log/error "Config is invalid, can't start server"))