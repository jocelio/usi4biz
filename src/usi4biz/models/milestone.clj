(ns usi4biz.models.milestone
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord milestone [id product name description due_date])

(defn find-all-milestones []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from milestone"])]
     rows)))

(defn find-by-product [product-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from milestone where product = ?" product-id])]
      rows)))

(defn find-milestone [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from milestone where id = ?" id])]
      rows)))

(defn create [milestone]
  (jdbc/insert! ds/db-spec :milestone (assoc milestone :id (ds/unique-id))))

;(create (milestone. nil
;                    "DA8B4D129F4849E18799084DC74EF790"
;                    "15.11.1.0"
;                    "First release of November"
;                    "2015-11-09"))
