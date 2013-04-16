(ns tailrecursion.birddog
  (:require [tailrecursion.birddog.log    :refer [info error]]
            [tailrecursion.birddog.db     :as    db]
            [tailrecursion.birddog.loiter :as loiter]))

(defn -main [& args]
  (info "starting")
  (db/load-schema!))
