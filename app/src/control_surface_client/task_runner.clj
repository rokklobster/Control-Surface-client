(ns control-surface-client.task-runner)

;; todo: storage for tasks
(defonce tasks (atom {}))
(defonce runtime (atom (Runtime/getRuntime)))

;; todo: fetch pair, if some - kill the process
(defn cancel-task [name]
  (reset! tasks (dissoc @tasks (keyword name)))
  true)

;; todo: 
;;  + safety checks (run type is shell or one of predefined; no su/sudo in code)
;;  call exec on runtime
;;  create a thread/green thread to fetch data from outputs
;;  create an on-close handler to kill the thread
;;  save pair of process instance + fetcher thread
(defn run-task [name type cmd cfg]
;;   todo: drop old task
;;   compose command array
  (reset! tasks (assoc @tasks (keyword name) type))
  true)

(defn query-tasks [] (map str (keys @tasks)))