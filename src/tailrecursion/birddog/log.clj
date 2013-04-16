(ns tailrecursion.birddog.log
  (:require [clojure.tools.logging :as log]))

(def dev?
  (delay (boolean (System/getenv "ENV"))))

(defmacro info [& args]
  (if @dev?
    `(println (java.util.Date.) "INFO" ~@args)
    `(log/info ~@args)))

(defmacro error [& args]
  (if @dev?
    `(println (java.util.Date.) "ERROR" ~@args)
    `(log/error ~@args)))
