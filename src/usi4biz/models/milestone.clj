(ns usi4biz.models.milestone
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord milestone [id product name description due_date, start_sprint, type])

(defn find-all-milestones []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from milestone where due_date >= now()"])]
     rows)))

(defn find-by-product [product-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from milestone where product = ?" product-id])]
      rows)))

(defn find-milestone [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from milestone where id = ?" id])]
      rows)))

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

(defn create [milestone]
  (jdbc/insert! ds/db-spec :milestone (assoc milestone :id (ds/unique-id))))

;(create (milestone. nil
;                    "DA8B4D129F4849E18799084DC74EF790"
;                    "15.11.2.0"
;                    "Second release of November"
;                    "2015-11-23"
;                    "2015-11-05 12:00:00"
;                    "MAJOR"))
;                    ;"INTERMEDIARY"))
