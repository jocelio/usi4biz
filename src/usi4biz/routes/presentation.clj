(ns usi4biz.routes.presentation
  (:require [compojure.core             :refer [defroutes GET]]
            [selmer.parser              :as selmer]
            [usi4biz.models.product     :as product]
            [usi4biz.models.issue-state :as issue-state]
            [usi4biz.models.milestone   :as milestone]))

(defn tabular-values []
  (map #(conj (val %) (key %))
       (loop [res    {}
              totals (milestone/group-workload-by-person)]
          (if (empty? totals)
            res
            (recur (assoc res (:first_name (first totals))
                              (conj (get res (:first_name (first totals))) (:total (first totals))))
                   (rest totals))))))

(defn index [acronym]
  (let [a-product           (first (product/find-by-acronym acronym))
        upcomming-milestone (first (milestone/upcomming-milestone))]
    (selmer/render-file "usi4biz/views/templates/presentation.html"
      {:product                 (:acronym a-product)
       :author                  "Hildeberto Mendonça, Ph.D."
       :backlog-size            (:size (first (issue-state/backlog-size)))
       :milestones              (map #(:name %) (milestone/find-all-milestones))
       :values                  (tabular-values)
       :upcomming-milestone     (:name upcomming-milestone)
       :total-planned-issues    (:total (first (issue-state/total-planned-issues (:id upcomming-milestone))))
       :total-unplanned-issues  (:total (first (issue-state/total-unplanned-issues)))
       :total-finished-issues   (:total (first (issue-state/total-finished-issues)))
       :total-unfinished-issues (:total (first (issue-state/total-unfinished-issues)))})))

(:total (first (issue-state/total-unplanned-issues)))

(defroutes routes
  (GET "/:acronym" [acronym] (index acronym)))
