(ns control-surface-client.task-runner
  (:require [clojure.core.async :refer [go]]
            [clojure.tools.logging :as log]
            [control-surface-client.models :refer [->TaskOutput]]
            [control-surface-client.orchestrator-api :refer [push-log]]
            [control-surface-client.util :refer [utc-now]]))

(defonce tasks (atom {}))
(defonce runtime (atom (Runtime/getRuntime)))
(defonce batch (atom []))

(defn run-sending-batches [cfg]
  (go (while true
        (when (> (count @batch) 0)
          (log/info "preparing to push " (count @batch) " items")
          ;; todo: catch
          (push-log @batch cfg)
          (reset! batch []))
        (Thread/sleep 5000))))

(defn add-item-to-batch [name v]
  (swap! batch conj (->TaskOutput name (str (utc-now)) v)))

(defn cancel-task [name]
  (let [p (get @tasks name)]
    (if (some? p)
      (do
        (.destroy (:process p))
        (swap! tasks dissoc name)
        true)
      false)))

(defn shutdown-all []
  (map #(-> % :process .destroy) (vals @tasks)))

(defn try-read [rdr]
  (try
    (when (.ready rdr) (.readLine rdr))
    (catch Exception _ nil)))

(defn can-read [rdr]
  (try
    (.ready rdr)
    (catch Exception _ nil)))

(defn execute-cmd-and-observe [args name]
  (let [p (.exec @runtime (into-array args))
        ir (.inputReader p)
        er (.errorReader p)]
    (go (do
          (while (or (.isAlive p) (can-read ir) (can-read er))
            (when-some [v (try-read ir)] (add-item-to-batch name v))
            (when-some [v (try-read er)] (add-item-to-batch name v))
            (Thread/sleep 500))
          (swap! tasks dissoc name)))
    (swap! tasks assoc name {:process p :readers [ir er]})))

(defn build-args [type cmd cfg]
  (let [rsc (-> cfg :rootSpaceCommands (get type))]
    (cond
      (= "shell" type) cmd
      (some? rsc) (into [] cat [["sudo"] rsc cmd])
      :else nil)))


(defn run-task [name type cmd cfg]
  (when-some [p (get @tasks name)] (.destroy (:process p)))
  (let [ivk (build-args type cmd cfg)]
    (execute-cmd-and-observe ivk name))
  true)

(defn query-tasks [] (map (fn [[k {p :process}]] {:name k :alive (.isAlive p)}) @tasks))

(defn tasks-cleanup []
  (log/info "performing cleanup...")
  (shutdown-all)
  (shutdown-agents))

(defn tasks-startup [cfg]
  (run-sending-batches cfg)
  (.addShutdownHook @runtime (Thread. ^Runnable tasks-cleanup)))