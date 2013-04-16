(ns tailrecursion.birddog
  (:require [tailrecursion.birddog.log    :refer [info error]]
            [tailrecursion.birddog.db     :as    db]
            [tailrecursion.birddog.loiter :as loiter]))

(defn -main [& cidrs]
  (info "starting")
  (db/load-schema!)
  (doseq [cidr cidrs]
    (loiter/loiter cidr :delay-ms 10000)))
