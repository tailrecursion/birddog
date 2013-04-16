(ns tailrecursion.birddog.db
  (:require [tailrecursion.birddog.log :refer [info error]]
            [datomic.api               :refer [q db] :as d]))

(def db-uri "datomic:mem://birddog")

;;; transactor fns

(def add-machine
  #db/fn {:lang "clojure"
          :params [db ip]
          :code (into []
                      (when-not (seq (q '[:find ?e
                                          :in $ ?ip
                                          :where [?e :birddog.machine/ipv4 ?ip]] db ip))
                        (let [t (java.util.Date.)]
                          [{:db/id #db/id [:db.part/user]
                            :birddog.machine/ipv4 ip
                            :birddog.machine/discovered t}])))})

(def schema
  [;; transactor fns
   {:db/ident :add-machine
    :db/doc "Add an IP iff it is unknown to us"
    :db/id #db/id [:db.part/db]
    :db/fn add-machine}

   ;; string => string map support
   {:db/ident :birddog.pair/key
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.pair/val
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   
   ;; processes
   {:db/ident :birddog.system..process/pid
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.system.process/user
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.system.process/cmd
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.system.process/etimes
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.system.process/n-children
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; filesystems
   {:db/ident :birddog.system.filesystem/size
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.system.filesystem/free
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.system.filesystem/dir
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; machines
   {:db/ident :birddog.machine/ipv4
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/discovered
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/probes
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}

   ;; probes
   {:db/ident :birddog.probe/started
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.probe/completed
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.probe/status
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.probe/handler    ;ns/var
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.probe/values
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}])

(defn load-schema!
  []
  (d/delete-database db-uri)
  (d/create-database db-uri)
  (d/transact (d/connect db-uri) schema))

(defn paint
  [ip]
  (d/transact (d/connect db-uri) [[:add-machine ip]]))

(defn start-probe
  [machine-id handler]
  (let [probe-id (d/tempid :db.part/user)]
    (d/transact (d/connect db-uri)
                [{:db/id probe-id
                  :birddog.probe/started (java.util.Date.)
                  :birddog.probe/handler handler}])
    probe-id))

(defn complete-probe
  [probe-id value]
  (d/transact (d/connect db-uri)
              [{:db/id probe-id
                :birddog.probe/completed (java.util.Date.)
                :birddog.probe/value value}]))

(comment
  (d/transact (d/connect db-uri) nil)

  (d/transact (d/connect db-uri) [[:add-machine "1.2.3.4"]])

  (q '[:find ?e :where [?e :birddog.machine/ipv4]] (db (d/connect db-uri)))

  (q '[:find ?e
     :in $ ?ip
     :where [?e :birddog.machine/ipv4 ?ip]] (db (d/connect db-uri)) "1.2.3.4"))
