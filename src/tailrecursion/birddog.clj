(ns tailrecursion.birddog
  (:require [tailrecursion.birddog.log :refer [info error]]
            [tailrecursion.birddog.db  :as    db]))

(defn -main [& args]
  (info "loading schema")
  (db/load-schema!))

(comment
  (exec :user "alan" :host "tailrecursion.com" :cmd "uname")
  )
