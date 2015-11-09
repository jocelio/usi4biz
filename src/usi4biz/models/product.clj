; Usi4Biz: User Interface For Business
; Copyright (C) 2015 Hildeberto Mendonça
;
; Contact email: hildeberto@usi4biz.com
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
  usi4biz.models.product
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [clojure.string     :as string]
            [usi4biz.datasource :as ds]
            [bouncer.validators :as v]))

(defrecord product [id name description acronym])

(def validation-rules {:name v/required})

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
  (jdbc/delete! ds/db-spec :product ["id = ?" id]) id)
