(ns usi4biz.models.issue
  (:require [hikari-cp.core             :refer :all]
            [clojure.java.jdbc          :as jdbc]
            [usi4biz.datasource         :as ds]
            [usi4biz.models.issue-state :as iss]))

(def assigning-types {:PLANNED            "PLANNED" ; as result of the sprint meeting
                      :UNPLANNED          "UNPLANNED" ; something to be patched or undisciplined
                      :UNPLANNED_APPROVED "UNPLANNED_APPROVED" ; unplanned but approved to be included with planned
                      })

(defrecord issue [id product reference name description milestone assignee effort priority assigning_type])

(defn find-by-reference [reference]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select * from issue where reference = ?" reference])))

(defn create [a-issue]
  (let [issue (assoc a-issue :id (ds/unique-id))]
    (jdbc/insert! ds/db-spec :issue issue)
    issue))
