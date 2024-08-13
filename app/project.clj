(defproject control-surface-client "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/rokklobster/Control-Surface-client"
  :license {:name "MIT"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure     "1.11.3"]
                 [ring/ring-core          "1.9.1"]
                 [ring/ring-jetty-adapter "1.9.1"]
                 [compojure               "1.6.2"]
                 [org.clojure/tools.logging "1.3.0"]
                 [org.slf4j/slf4j-api "2.0.9"]
                 [ch.qos.logback/logback-classic "1.4.11"]
                 [net.logstash.logback/logstash-logback-encoder "7.4"]
                 [ch.codesmith/logger "0.7.108"]
                 [org.clojure/data.json "2.5.0"]
                 [ring/ring-json "0.5.1"]
                 [clojure.java-time "1.4.2"]
                 [org.clojure/core.async "1.6.673"]
                 [clj-http "3.13.0"]]
  :repl-options {:init-ns control-surface-client.core}
  :main control-surface-client.core)
