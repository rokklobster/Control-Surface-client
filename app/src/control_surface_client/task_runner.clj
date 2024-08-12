(ns control-surface-client.task-runner)

;; todo: storage for tasks
(defonce tasks (atom {}))

(defn cancel-task [name] 
  (reset! tasks (dissoc @tasks (keyword name)))
  true)

(defn run-task [name type cmd cfg] 
  (reset! tasks (assoc @tasks (keyword name) type))
  true)

(defn query-tasks [] (keys @tasks))