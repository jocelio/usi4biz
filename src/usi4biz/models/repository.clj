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
  usi4biz.models.repository
  (:require [clojure.java.jdbc          :as jdbc]
            [usi4biz.datasource         :as ds]
            [bouncer.validators         :as v]))

(def validation-rules {:product v/required
                       :user    v/required
                       :name    v/required})

(defn find-by-product [a-product]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select * from repository where product = ?" (:id a-product)])))

(defn save [a-repository]
  (if (empty? (:id a-repository))
    (let [repository (assoc a-repository :id (ds/unique-id))]
      (jdbc/insert! ds/db-spec :repository repository)
      repository)
    (let [repository a-repository]
      (jdbc/update! ds/db-spec :repository repository ["id = ?" (:id repository)])
      repository)))
