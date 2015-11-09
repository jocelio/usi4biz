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
  usi4biz.routes.chart
  (:require [clojure.data.json          :as json]
            [usi4biz.models.issue-state :as issue-state]
            [usi4biz.utils.calendar     :as calendar]
            [compojure.core             :refer [context defroutes GET]]
            [liberator.core             :refer [defresource]]))

(defn labels []
  (distinct (map #(str (calendar/month-name (:month %)) "-" (:year %))
                 (concat (first (map #(issue-state/find-total-state-per-month (val %))
                                     issue-state/states))))))

(defn total-labels [state]
  (let [total-state-per-month (issue-state/find-total-state-per-month state)]
    (apply assoc {} (interleave (map #(keyword (str (calendar/month-name (:month %)) "-" (:year %)))
                                     total-state-per-month)
        (map #(:total %) total-state-per-month)))))

(defn totals [state]
  (map #(let [value (% (total-labels state))]
          (if (nil? value) 0 value))
       (map keyword (labels))))

(defresource line-chart []
  :available-media-types ["application/json"]
  :handle-ok (fn [_]
               (let [labels (labels)
                     states issue-state/states]
                 (json/write-str {:labels labels
                                  :datasets [{:label "Created",
                                              :fillColor "rgba(220,220,220,0.2)",
                                              :strokeColor "rgba(255,0,0,1)",
                                              :pointColor "rgba(220,220,220,1)",
                                              :pointStrokeColor "#fff",
                                              :pointHighlightFill "#fff",
                                              :pointHighlightStroke "rgba(220,220,220,1)",
                                              :data (totals (:CREATED states))}

                                             {:label "Assigned",
                                              :fillColor "rgba(151,187,205,0.2)",
                                              :strokeColor "rgba(255,128,0,1)",
                                              :pointColor "rgba(151,187,205,1)",
                                              :pointStrokeColor "#fff",
                                              :pointHighlightFill "#fff",
                                              :pointHighlightStroke "rgba(151,187,205,1)",
                                              :data (totals (:ASSIGNED states))}

                                             {:label "Finished",
                                              :fillColor "rgba(220,220,220,0.2)",
                                              :strokeColor "rgba(0,255,0,1)",
                                              :pointColor "rgba(220,220,220,1)",
                                              :pointStrokeColor "#fff",
                                              :pointHighlightFill "#fff",
                                              :pointHighlightStroke "rgba(220,220,220,1)",
                                              :data (totals (:FINISHED states))}

                                             {:label "Closed",
                                              :fillColor "rgba(151,187,205,0.2)",
                                              :strokeColor "rgba(0,255,255,1)",
                                              :pointColor "rgba(151,187,205,1)",
                                              :pointStrokeColor "#fff",
                                              :pointHighlightFill "#fff",
                                              :pointHighlightStroke "rgba(151,187,205,1)",
                                              :data (totals (:CLOSED states))}

                                             {:label "Canceled",
                                              :fillColor "rgba(220,220,220,0.2)",
                                              :strokeColor "rgba(255,0,255,1)",
                                              :pointColor "rgba(220,220,220,1)",
                                              :pointStrokeColor "#fff",
                                              :pointHighlightFill "#fff",
                                              :pointHighlightStroke "rgba(220,220,220,1)",
                                              :data (totals (:CANCELED states))}]}))))

(defroutes routes
  (context "/charts" []
    (GET "/line" [] (line-chart))))
