(ns control-surface-client.util
  (:require [clojure.data.json :as json]
            [control-surface-client.models :refer [->ApiResponse]]
            [java-time.api :as jt]))

(defn api-response [scode status msg & payload]
  {:status scode
   :body (json/write-str (->ApiResponse status msg (first payload)))
   :headers {"Content-Type" "application/json"}})

(defn utc-now [] (-> "UTC" jt/zone-id jt/zoned-date-time))