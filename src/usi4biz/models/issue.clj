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
