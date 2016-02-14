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
  usi4biz.models.milestone
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]
            [bouncer.validators :as v]))

(def types {:MAJOR        "MAJOR" ; planned milestone with fixed periodicity
            :INTERMEDIARY "INTERMEDIARY"}) ; unplanned milestone to apply a patch.

(def validation-rules {:product v/required
                       :name    v/required
                       :type    v/required})

(defn find-it [id]
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (first (jdbc/query conn ["select m.id, m.product, p.name as product_name, m.name, m.description, m.due_date, m.start_sprint, m.type
                             from milestone m join product p on m.product = p.id
                             where m.id = ?" id]))))

(defn find-by-product [product-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select id, name from milestone where product = ? order by due_date desc" product-id])))

(defn upcomming-milestone []
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select * from milestone where type = 'MAJOR' and due_date >= now() and start_sprint <= now()"])))

(defn group-workload-by-person []
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select p.first_name, m.name as reference, count(i.reference) total
                       from person p right join issue i on p.id = i.assignee right join milestone m on i.milestone = m.id
                       where i.id not in (select issue
                                          from issue_state
                                          where state = 'FINISHED' or state = 'CLOSED' or state = 'CANCELED')
                       group by p.first_name, m.name
                       order by m.due_date desc"])))

(defn save [a-milestone]
 (if (empty? (:id a-milestone))
   (let [milestone (assoc a-milestone :id (ds/unique-id))]
     (jdbc/insert! ds/db-spec :milestone milestone)
     milestone)
   (let [milestone a-milestone]
     (jdbc/update! ds/db-spec :milestone milestone ["id = ?" (:id milestone)])
     milestone)))

(defn delete [id]
 (jdbc/delete! ds/db-spec :milestone ["id = ?" id]) id)
