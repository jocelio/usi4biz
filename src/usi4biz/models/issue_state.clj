(ns usi4biz.models.issue-state
  (:require [hikari-cp.core         :refer :all]
            [clojure.java.jdbc      :as jdbc]
            [usi4biz.datasource     :as ds]
            [usi4biz.models.issue   :as issue]
            [usi4biz.utils.calendar :as calendar]))

(def states {:CREATED "CREATED" :ASSIGNED "ASSIGNED" :FINISHED "FINISHED" :CLOSED "CLOSED" :CANCELED "CANCELED"})

(defrecord issue-state [id issue state set_date])

(defn backlog-size []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (jdbc/query conn ["select count(id) size
                      from issue
                      where id not in (select issue
                                       from issue_state
                                       where state in ('FINISHED','CLOSED','CANCELED'))"])))

(defn find-by-issue [issue-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from issue_state where issue = ?" issue-id])]
      rows)))

(defn find-total-state-per-month [state]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select year(set_date) year, month(set_date) month, count(issue) total
                       from issue_state
                       where state = ?
                       group by year(set_date), month(set_date)
                       order by set_date" state])))

(defn accumulate-total-per-month [state]
  (let [totals (find-total-state-per-month state)]
    (loop [totals                  totals
           accumulated             (:total (first totals))
           totals-with-accumulated []]
      (if (empty? totals)
        totals-with-accumulated
        (recur (rest totals)
               (+ accumulated (if (<= (count totals) 1)
                                  0
                                  (:total (second totals))))
               (conj totals-with-accumulated (assoc (first totals)
                                                    :accumulated accumulated)))))))

(defn create [issue-state]
  (jdbc/insert! ds/db-spec :issue_state (assoc issue-state :id (ds/unique-id))))

;(let [ticket (:id (first (issue/find-by-reference "EPCJB-391")))]
;  (create (issue-state. nil
;                ticket
;                ;"CREATED"
;                ;"ASSIGNED"
;                ;"FINISHED"
;                ;"CLOSED"
;                "CANCELED"
;                "2015-10-06 23:28:00")))
