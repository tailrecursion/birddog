(ns tailrecursion.birddog.net
  (:require [tailrecursion.birddog.log :refer [info error]]))

(defn port-open? [ip port & {:keys [timeout]
                             :or {timeout 1000}}]
  {:pre [(re-matches #"\d+.\d+.\d+.\d+" ip)]}
  (let [sock (java.net.Socket.)]
    (try
      (boolean (doto (java.net.Socket.)
                 (.connect (java.net.InetSocketAddress. ip port) timeout)
                 (.close)))
      (catch Exception e false))))

(defn cidr-info [cidr]
  (bean (.getInfo (doto (org.apache.commons.net.util.SubnetUtils. cidr)
                    (.setInclusiveHostCount true)))))

(defn addresses-in [cidr]
  (-> cidr cidr-info :allAddresses set))

(defn propmap [m]
  (reduce (fn [p [k v]] (doto p (.put k v))) (java.util.Properties.) m))

(defn exec [& {:keys [user host port id cmd check-host-key timeout-ms]
               :or {user "root"
                    port 22
                    id (str (System/getenv "HOME") "/.ssh/id_rsa")
                    check-host-key false
                    timeout-ms 0}}]
  (let [jsch    (doto (com.jcraft.jsch.JSch.)
                  (.addIdentity id))
        session (doto (.getSession jsch user host port)
                  (.setConfig (propmap {"StrictHostKeyChecking"
                                        (if check-host-key "yes" "no")}))
                  (.setTimeout timeout-ms)
                  (.connect))
        channel (doto (.openChannel session "exec")
                  (.setCommand cmd)
                  (.connect))]
    (let [out (slurp (.getInputStream channel))
          ret {:exit (.getExitStatus channel)
               :out out}]
      (.disconnect channel)
      (.disconnect session)
      ret)))

(comment
  (exec :user "alan" :host "tailrecursion.com" :cmd "uname")
  )
