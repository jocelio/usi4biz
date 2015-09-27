(ns usi4biz.models.product
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord product [id name description acronym])

(defn find-all-products []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from product"])]
     rows)))

(defn find-product [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from product where id = ?" id])]
      rows)))

(defn create [product]
  (jdbc/insert! ds/db-spec :product (assoc product :id (ds/unique-id))))

;(create (product. nil
;                  "Aide Social"
;                  "Application for the service of Social aid."
;                  "AIDE"))
