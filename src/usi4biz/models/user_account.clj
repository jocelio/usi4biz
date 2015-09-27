(ns usi4biz.models.user-account
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord user-account [id username])

(defn find-all-accounts []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from user_account"])]
     rows)))

(defn find-by-name [name]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from user_account where name = ?" name])]
      rows)))

(defn create [user-account]
  (jdbc/insert! ds/db-spec :user_account (assoc user-account :id (ds/unique-id))))

;(create (user-account. nil "enizeyimana"))
