(ns control-surface-client.models)

(defrecord Options
           [usePull
            pullInterval
            orchestratorUrl
            ownUrl
            port
            ownName
            userToken
            rootSpaceCommands])

(defrecord ApiResponse
           [success
            message
            payload])

(defrecord TaskRunRequest
           [taskName
            taskCommand
            taskType
            scheduledAt
            targetServer])

(defrecord TaskCancelRequest
           [taskName targetServer])

(defrecord SelfLogDb
           [id
            level
            timestamp
            message
            pushed])

(defrecord SelfLogApi
           [client level timestamp message])

(defrecord TaskOutput
           [taskName
            timestamp
            text])