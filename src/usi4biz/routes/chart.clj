(ns usi4biz.routes.chart
  (:require [clojure.data.json          :as json]
            [usi4biz.models.issue-state :as issue-state]
            [usi4biz.utils.calendar     :as calendar]
            [compojure.core             :refer [context defroutes GET]]
            [liberator.core             :refer [defresource]]))

(defresource line-chart []
  :available-media-types ["application/json"]
  :handle-ok (fn [_]
               (let [labels       (issue-state/labels)]
                 (json/write-str {:labels labels
                                        :datasets [{:label "Created",
                                                    :fillColor "rgba(220,220,220,0.2)",
                                                    :strokeColor "rgba(255,0,0,1)",
                                                    :pointColor "rgba(220,220,220,1)",
                                                    :pointStrokeColor "#fff",
                                                    :pointHighlightFill "#fff",
                                                    :pointHighlightStroke "rgba(220,220,220,1)",
                                                    :data (issue-state/totals (:CREATED issue-state/states))}

                                                   {:label "Assigned",
                                                    :fillColor "rgba(151,187,205,0.2)",
                                                    :strokeColor "rgba(255,128,0,1)",
                                                    :pointColor "rgba(151,187,205,1)",
                                                    :pointStrokeColor "#fff",
                                                    :pointHighlightFill "#fff",
                                                    :pointHighlightStroke "rgba(151,187,205,1)",
                                                    :data (issue-state/totals (:ASSIGNED issue-state/states))}

                                                   {:label "Finished",
                                                    :fillColor "rgba(220,220,220,0.2)",
                                                    :strokeColor "rgba(0,255,0,1)",
                                                    :pointColor "rgba(220,220,220,1)",
                                                    :pointStrokeColor "#fff",
                                                    :pointHighlightFill "#fff",
                                                    :pointHighlightStroke "rgba(220,220,220,1)",
                                                    :data (issue-state/totals (:FINISHED issue-state/states))}

                                                   {:label "Closed",
                                                    :fillColor "rgba(151,187,205,0.2)",
                                                    :strokeColor "rgba(0,255,255,1)",
                                                    :pointColor "rgba(151,187,205,1)",
                                                    :pointStrokeColor "#fff",
                                                    :pointHighlightFill "#fff",
                                                    :pointHighlightStroke "rgba(151,187,205,1)",
                                                    :data (issue-state/totals (:CLOSED issue-state/states))}

                                                   {:label "Canceled",
                                                    :fillColor "rgba(220,220,220,0.2)",
                                                    :strokeColor "rgba(255,0,255,1)",
                                                    :pointColor "rgba(220,220,220,1)",
                                                    :pointStrokeColor "#fff",
                                                    :pointHighlightFill "#fff",
                                                    :pointHighlightStroke "rgba(220,220,220,1)",
                                                    :data (issue-state/totals (:CANCELED issue-state/states))}]}))))

(defroutes routes
  (context "/charts" []
    (GET "/line" [] (line-chart))))
