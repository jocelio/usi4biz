(ns usi4biz.datasource
  (:require [clojure.java.jdbc    :as jdbc]
            [clojure.edn          :as edn]
            [hikari-cp.core       :refer :all]
            [joplin.core          :as joplin]
            [joplin.jdbc.database]))


(defn db-config []
  (with-open [in (java.io.PushbackReader.
                   (clojure.java.io/reader "resources/db-config.edn"))]
    (edn/read in)))

(db-config)

(def datasource
  (make-datasource (db-config)))

(defn close []
  (close-datasource datasource))

(def db-spec
   (let [db-conf   (db-config)
         classname   "com.mysql.jdbc.Driver"
         subprotocol (:adapter  db-conf)
         subname     (str "//"  (:server-name   db-conf)
                          ":"   (:port-number   db-conf)
                          "/"   (:database-name db-conf))
         user        (:username db-conf)
         password    (:password db-conf)]
  (zipmap [:classname :subprotocol :subname :user :password]
          [ classname  subprotocol  subname  user  password])))

(defn all-tables []
    (map #(% :table_name)
         (jdbc/with-db-metadata [md db-spec]
           (jdbc/metadata-result (.getTables md nil nil nil (into-array ["TABLE"]))))))

(def joplin-target {:db {:type :sql
                         :url (str "jdbc:" (:subprotocol db-spec) ":" (:subname  db-spec)
                                   "?user=" (:user db-spec) "&password=" (:password db-spec))}
                    :migrator "resources/migrators/sql"})

(defn migrate-db []
  (joplin/migrate-db joplin-target))

(defn unique-id []
  (.replace (.toUpperCase (str (java.util.UUID/randomUUID))) "-" ""))
