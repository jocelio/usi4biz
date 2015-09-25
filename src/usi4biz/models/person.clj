(ns usi4biz.models.person
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord person [id first-name last-name email user-account])

(defn find-all-persons []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from person"])]
     rows)))

(defn find-person [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from person where id = ?" id])]
      rows)))
