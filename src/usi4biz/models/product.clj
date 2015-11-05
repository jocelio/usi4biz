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

(defn create [a-product]
  (let [product (assoc a-product :id (ds/unique-id))]
    (jdbc/insert! ds/db-spec :product product)
    product))
