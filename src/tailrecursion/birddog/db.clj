(ns tailrecursion.birddog.db
  (:require [tailrecursion.birddog.log :refer [info error]]
            [datomic.api               :refer [q db] :as d]))

(def db-uri "datomic:mem://birddog")

(def schema
  [;; targets
   {:db/ident :birddog.target/ipv4
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.target/discovered
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   
   ;; processes
   {:db/ident :birddog.process/pid
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.process/user
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.process/cmd
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.process/etimes
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.process/n-children
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}

   ;; filesystems
   {:db/ident :birddog.filesystem/size
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.filesystem/free
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.filesystem/dir
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
   {:db/ident :birddog.machine/hostname
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/loadavg1
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/float
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/loadavg5
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/float
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/loadavg15
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/float
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/discovered
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/last-scanned
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/status
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/memtotal
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/memfree
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/processes
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.machine/filesystems
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db.install/_attribute :db.part/db}

   ;; probes
   {:db/ident :birddog.probe/id
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/ident :birddog.probe/ipv4
    :db/id #db/id [:db.part/db]
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
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
    :db.install/_attribute :db.part/db}])

(defn load-schema!
  []
  (d/delete-database db-uri)
  (d/create-database db-uri)
  (d/transact (d/connect db-uri) schema)
  (info "Loaded schema"))

(defn add-target
  [ip]
  (d/transact (d/connect db-uri)
              [{:db/id #db/id [:db.part/user]
                :birddog.target/ipv4 ip
                :birddog.target/discovered (java.util.Date.)}]))

(defn rm-target
  [ip]
  (let [conn (d/connect db-uri)
        query '[:find ?e :in $ ?ip :where [?e :birddog.target/ipv4 ?ip]]
        eid (ffirst (q query (db conn) ip))]
    (d/transact conn [[:db.fn/retractEntity eid]])))

(defn resmap
  [db e]
  (into {} (for [[k v] (d/entity db (first e))]
             [(keyword (name k)) v])))

(defn open-targets
  []
  (let [conn  (d/connect db-uri)
        query '[:find ?e :where [?e :birddog.target/ipv4]]
        db     (db conn)]
    (mapv (partial resmap db) (q query db))))

(defn start-probe
  [ip]
  (let [probe-id (java.util.UUID/randomUUID)]
    (d/transact (d/connect db-uri)
                [{:db/id #db/id [:db.part/user]
                  :birddog.probe/id probe-id
                  :birddog.probe/ipv4 ip
                  :birddog.probe/started (java.util.Date.)}])
    probe-id))

(defn complete-probe
  [probe-id & [status]]
  (let [conn (d/connect db-uri)
        query '[:find ?e :in $ ?uuid :where [?e :birddog.probe/id ?uuid]]
        eid (ffirst (q query (db conn) probe-id))]
    (d/transact conn
                [{:db/id eid
                  :birddog.probe/completed (java.util.Date.)
                  :birddog.probe/status (or status "complete")}
                 [:db/retract eid :birddog.probe/id probe-id]])))

(defn pending-probes
  []
  (let [conn  (d/connect db-uri)
        query '[:find ?e :where [?e :birddog.probe/id]]
        db     (db conn)]
    (mapv (partial resmap db) (q query db))))