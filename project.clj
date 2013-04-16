(defproject tailrecursion/birddog "0.1.0-SNAPSHOT"
  :description "Covey riding"
  :url "https://github.com/tailrecursion/birddog"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.datomic/datomic-free "0.8.3889"
                  :exclusions [org.clojure/clojure
                               org.slf4j/slf4j-nop
                               org.slf4j/log4j-over-slf4j]]
                 [com.jcraft/jsch "0.1.49"]
                 [commons-net "3.2"]
                 [org.clojure/tools.logging "0.2.6"]
                 [log4j "1.2.15"
                  :exclusions [javax.mail/mail
                               javax.jms/jms
                               com.sun.jdmk/jmxtools
                               com.sun.jmx/jmxri]]
                 [org.slf4j/slf4j-log4j12 "1.6.6"]]
  :resource-paths ["config"]
  :main ^:skip-aot tailrecursion.birddog)
