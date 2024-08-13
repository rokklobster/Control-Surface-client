(ns control-surface-client.task-runner
  (:require [clojure.core.async :refer [go]]
            [clojure.tools.logging :as log]
            [control-surface-client.orchestrator-api :refer [push-log]]))

(defonce tasks (atom {}))
(defonce runtime (atom (Runtime/getRuntime)))

(defn cancel-task [name]
  (let [k (keyword name)
        p (get @tasks k)]
    (if (some? p)
      (do
        (.destroy p)
        (swap! tasks dissoc k)
        true)
      false)))

(defn shutdown-all []
  (map #((.destroy %)) (vals @tasks)))

(defn try-read [rdr]
  (try
    (when (.ready rdr) (.readLine rdr))
    (catch Exception _ nil)))

(defn can-read [rdr]
  (try
    (.ready rdr)
    (catch Exception _ nil)))

(defn execute-cmd-and-observe [args name cfg]
  (let [p (.exec @runtime (into-array args))
        ir (.inputReader p)
        er (.errorReader p)]
    (go (do
          (while (or (.isAlive p) (can-read ir) (can-read er))
            (when-some [v (try-read ir)] (push-log name v cfg))
            (when-some [v (try-read er)] (push-log name v cfg)))
          (swap! tasks dissoc (keyword name))))
    (swap! tasks assoc (keyword name) {:process p :readers [ir er]})))

(defn run-task [name type cmd cfg]
  (when-some [p (get @tasks (keyword name))] (.destroy p))
  (let [ivk (if (= "shell" type) cmd (into [] cat [["sudo"] (-> cfg :rootSpaceCommands (get (keyword type))) cmd]))]
    (execute-cmd-and-observe ivk name cfg))
  true)

(defn query-tasks [] (map (fn [[k {p :process}]] {:name k :alive (.isAlive p)}) @tasks))

(defn tasks-cleanup []
  (log/info "performing cleanup...")
  (shutdown-all)
  (shutdown-agents))

(defn tasks-startup [_]
  (.addShutdownHook @runtime (Thread. ^Runnable tasks-cleanup)))