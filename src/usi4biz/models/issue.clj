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
  usi4biz.models.issue
  (:require [hikari-cp.core             :refer :all]
            [clojure.java.jdbc          :as jdbc]
            [clojure.string             :refer [blank? upper-case split]]
            [usi4biz.datasource         :as ds]
            [usi4biz.models.issue-state :as iss]
            [bouncer.validators         :as v]
            [tentacles.issues           :as issues]))

(def assigning-types {; as result of the sprint meeting
                      :PLANNED            "PLANNED"
                      ; something to be patched or undisciplined
                      :UNPLANNED          "UNPLANNED"
                      ; unplanned but approved to be included with planned
                      :UNPLANNED_APPROVED "UNPLANNED_APPROVED"})

(def priority-types {:IMPORTANT "IMPORTANT"
                     :ESSENTIAL "ESSENTIAL"
                     :BLOCKING  "BLOCKING"})

(def validation-rules {:product   v/required
                       :name      v/required
                       :reference v/required})

(defn find-it [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (first (jdbc/query conn ["select i.id, i.name, i.description, i.product, p.name as product_name,
                                     i.milestone, m.name as milestone_name, i.assignee,
                                     a.first_name as assignee_name, i.priority,
                                     i.reference, assigning_type
                              from issue i join product p on i.product = p.id
                                           join milestone m on i.milestone = m.id
                                           join person a on i.assignee = a.id
                              where i.id = ?" id]))))

(defn find-by-repository [a-repository auth]
  (filter #(not (contains? % :pull_request))
          (issues/issues (:user a-repository)
                         (:name a-repository)
                         {:auth auth :per-page 100})))

(defn search [params]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn [(str "select i.id, i.name, i.milestone,
                                   m.name as milestone_name, i.assignee,
                                   concat(a.first_name, ' ', a.last_name) as assignee_name, i.priority,
                                   i.reference, assigning_type
                            from issue i join milestone m on i.milestone = m.id
                                         join person a on i.assignee = a.id"
                            (if (empty? params)
                              " where i.assignee is null or i.milestone is null"
                              (str " where i.id is not null"
                                  (if (blank? (:reference params))
                                    (str (if (not (blank? (:product params)))
                                           (str " and i.product = '" (:product params) "'"))
                                         (if (not (blank? (:milestone params)))
                                           (str " and i.milestone = '" (:milestone params) "'"))
                                         (if (not (blank? (:assignee params)))
                                           (str " and i.assignee = '" (:assignee params) "'")))
                                    (str " and UPPER(i.reference) = '" (upper-case (:reference params)) "'")))))])))

(defn find-by-reference [reference]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select * from issue where reference = ?" reference])))

(defn save [an-issue an-issue-state]
  (let [issue (if (empty? (:id an-issue))
                (let [i (assoc an-issue :id (ds/unique-id))]
                  (jdbc/insert! ds/db-spec :issue i)
                  i)
                (let [i an-issue]
                  (jdbc/update! ds/db-spec :issue i ["id = ?" (:id i)])
                  i))]
     (iss/save (assoc an-issue-state :issue (:id issue)))
     issue))

(defn delete [id]
  (jdbc/delete! ds/db-spec :issue ["id = ?" id]) id)
