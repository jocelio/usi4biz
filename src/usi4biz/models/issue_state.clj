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

(defn labels []
  (distinct (map #(str (calendar/month-name (:month %)) "-" (:year %))
                 (concat (first (map #(find-total-state-per-month (val %)) states))))))

(defn total-labels [state]
  (let [total-state-per-month (find-total-state-per-month state)]
    (apply assoc {} (interleave (map #(keyword (str (calendar/month-name (:month %)) "-" (:year %))) total-state-per-month)
        (map #(:total %) total-state-per-month)))))

(defn totals [state]
  (map #(let [value (% (total-labels state))]
          (if (nil? value) 0 value))
       (map keyword (labels))))

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

;(let [ticket (:id (first (issue/find-by-reference "EPC-9381")))]
;  (create (issue-state. nil
;                ticket
;                "CREATED"
;                ;"ASSIGNED"
;                ;"FINISHED"
;                ;"CLOSED"
;                ;"CANCELED"
;                "2015-09-28 10:42:00"))
;                ;"2015-09-24 15:00:00"))
;)
