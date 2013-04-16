(ns tailrecursion.birddog.loiter
  (:require [tailrecursion.birddog.log :refer [info error]]
            [tailrecursion.birddog.db  :refer [paint]]
            [tailrecursion.birddog.net :as    net])
  (:import (java.util.concurrent ScheduledThreadPoolExecutor
                                 TimeUnit
                                 CountDownLatch)))

(defn fixed-delay-executor [f delay-ms]
  (doto (ScheduledThreadPoolExecutor. 1)
    (.scheduleWithFixedDelay f 0 delay-ms TimeUnit/MILLISECONDS)))

(defn scan-ips
  [ips]
  (let [latch (CountDownLatch. (count ips))
        open  (atom 0)
        start-time (System/currentTimeMillis)
        check (fn [ip]
                (when (net/port-open? ip 22)
                  (swap! open inc)
                  (info (format "%s:22 open" ip))
                  (paint ip))
                (.countDown latch))]
    (doseq [agt (map agent ips)] (send-off agt check))
    (.await latch)                      ;TODO timeout
    (info (format "scanned %s IPs in %s seconds; %s were open."
                  (count ips)
                  (float (/ (- (System/currentTimeMillis) start-time)
                            1000))
                  @open))))

(defn loiter
  [cidr & {:keys [delay-ms]
           :or {delay-ms 10000}}]
  (let [cinfo (net/cidr-info cidr)]
    (fixed-delay-executor
     #(do (info (format "scanning port 22 on %s (%s through %s), every %s ms"
                        cidr
                        (:lowAddress cinfo)
                        (:highAddress cinfo)
                        delay-ms))
          (scan-ips (:allAddresses cinfo)))
     delay-ms)))
