(ns usi4biz.models.user-account
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord user-account [id name github-id])

(defn find-all-accounts []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from user_account"])]
     rows)))

(defn find-by-name [name]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from user_account where name = ?" name])]
      rows)))

(defn find-by-githugid [github-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from user_account where github_id = ?" id])]
      rows)))
