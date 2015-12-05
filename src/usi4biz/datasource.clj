; Usi4Biz: User Interaction For Business
; Copyright (C) 2015 Hildeberto Mendonça
;
; This program is free software: you can redistribute it and/or modify
; it under the terms of the GNU General Public License as published by
; the Free Software Foundation, either version 3 of the License, or
; (at your option) any later version.
;
; This program is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU General Public License for more details.
;
; You should have received a copy of the GNU General Public License
; along with this program. If not, see http://www.gnu.org/licenses/.

(ns ^{:author "Hildeberto Mendonça - hildeberto.com"}
  usi4biz.datasource
  (:require [clojure.java.jdbc      :as jdbc]
            [clojure.java.shell     :refer (sh)]
            [clojure.edn            :as edn]
            [hikari-cp.core         :refer :all]
            [joplin.core            :as joplin]
            [joplin.jdbc.database]
            [usi4biz.utils.calendar :as calendar]))

(defn db-config []
  (with-open [in (java.io.PushbackReader.
                   (clojure.java.io/reader "resources/db-config.edn"))]
    (edn/read in)))

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

(defn database-dump []
  (let [db-conf (db-config)]
    (sh "mysqldump" (str "--host=" (:server-name db-conf))
                    (str "--user=" (:username db-conf))
                    (str "--password=" (:password db-conf))
                    (str "--result-file=data/backup_usi4biz.sql")
                    (:database-name db-conf))))

(defn dump-import []
  (let [db-conf (db-config)]
    (sh "mysql" "-u" (:username db-conf)
                "-p" (:password db-conf)
                (:database-name db-conf)
                "<" "backup_usi4biz.sql")))

(defn format-date-db [str-date format]
  (calendar/to-string (calendar/to-date str-date format) "yyyy-MM-dd"))

(defn format-timestamp-db [str-timestamp format]
    (calendar/to-string (calendar/to-date str-timestamp format) "yyyy-MM-dd HH:mm"))
