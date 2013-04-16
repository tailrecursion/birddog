(ns tailrecursion.birddog.loiter
  (:require [tailrecursion.birddog.log :refer [info error]]
            [tailrecursion.birddog.db  :refer [paint]]
            [tailrecursion.birddog.net :as    net]))

(defn check-ssh
  [ip]
  (when (net/port-open? ip 22)
    (info (format "%s:22 is open" ip))
    (paint ip)))

(defn scan
  [cidr]
  (let [cinfo (net/cidr-info cidr)]
    (info (format "scanning port 22 on %s (%s through %s)"
                  cidr
                  (:lowAddress cinfo)
                  (:highAddress cinfo)))
    (doseq [agt (map agent (:allAddresses cinfo))]
      (send-off agt check-ssh))))
