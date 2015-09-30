(ns usi4biz.models.issue-state
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord issue-state [id issue state set_date])

(defn find-by-issue [issue-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from issue_state where issue = ?" issue-id])]
      rows)))

(defn create [issue-state]
  (jdbc/insert! ds/db-spec :issue_state (assoc issue-state :id (ds/unique-id))))

(create (issue-state. nil
                "78122021B8DD45FABDD2AC278E9678D1"
                "CREATED"
                ;"ASSIGNED"
                ;"FINISHED"
                ;"CLOSED"
                "2015-09-29 15:00:00"))
                ;"2015-09-24 15:00:00"))
