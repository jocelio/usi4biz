(ns usi4biz.models.product
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [clojure.string     :as string]
            [usi4biz.datasource :as ds]))

(defrecord product [id name description acronym])

(defn find [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (first (jdbc/query conn ["select * from product where id = ?" id]))))

(defn find-all []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (jdbc/query conn ["select * from product"])))

(defn save [a-product]
  (if (empty? (:id a-product))
    (let [product (assoc a-product :id (ds/unique-id))]
      (jdbc/insert! ds/db-spec :product product)
      product)
    (let [product a-product]
      (jdbc/update! ds/db-spec :product product ["id = ?" (:id product)])
      product)))

(defn delete [id]
  (jdbc/delete! ds/db-spec :product ["id = ?" id])
  id)
