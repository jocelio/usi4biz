(ns usi4biz.models.product
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [clojure.string     :as string]
            [usi4biz.datasource :as ds]))

(defrecord product [id name description acronym])

(defn find-all-products []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from product"])]
     rows)))

(defn find-by-acronym [acronym]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from product where acronym = ?" (string/upper-case acronym)])]
      rows)))

(defn create [product]
  (jdbc/insert! ds/db-spec :product (assoc product :id (ds/unique-id))))
