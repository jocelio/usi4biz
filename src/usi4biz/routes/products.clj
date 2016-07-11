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
  usi4biz.routes.products
  (:require [compojure.core             :refer [defroutes context DELETE GET POST]]
            [noir.session               :as session]
            [usi4biz.models.product     :as product]
            [usi4biz.models.issue       :as issue]
            [usi4biz.models.issue-state :as issue-state]
            [usi4biz.models.milestone   :as milestone]
            [usi4biz.models.repository  :as repository]
            [usi4biz.views.layout       :as layout]
            [bouncer.core               :as b]))

(defn products []
  (layout/render "products.html"
                 {:products (product/find-all)}))

(defn a-product [id]
  (let [product      (product/find-it id)
        repositories (map #(assoc % :issues (issue/find-by-repository % (session/get :auth)))
                          (repository/ product))]
    (layout/render "product.html"
                   {:product      product
                    :repositories repositories})))

(defn save-product [product]
  (if (b/valid? product product/validation-rules)
    (layout/render "products.html"
                   {:product (product/save product)
                    :products (product/find-all)})
    (layout/render "product_form.html"
                   {:product product
                    :error-name (first (:name (first (b/validate product product/validation-rules))))})))

(defn product-form
  ([]   (layout/render "product_form.html" {}))
  ([id] (layout/render "product_form.html" {:product (product/find-it id)})))

(defn tabular-values []
  (map #(conj (val %) (key %))
       (loop [res    {}
              totals (milestone/group-workload-by-person)]
          (if (empty? totals)
            res
            (recur (assoc res (:first_name (first totals))
                              (conj (get res (:first_name (first totals))) (:total (first totals))))
                   (rest totals))))))

(defn presentation [product-id]
  (let [a-product           (first (product/find-it product-id))
        upcomming-milestone (first (milestone/upcomming-milestone))]
    (layout/render "presentation.html"
                   {:product                 (:acronym a-product)
                    :author                  "Hildeberto Mendonça, Ph.D."
                    :backlog-size            (:size (first (issue-state/backlog-size)))
                    :milestones              (map #(:name %) (milestone/ product-id))
                    :values                  (tabular-values)
                    :upcomming-milestone     (:name upcomming-milestone)
                    :total-planned-issues    (:total (first (issue-state/total-planned-issues (:id upcomming-milestone))))
                    :total-unplanned-issues  (:total (first (issue-state/total-unplanned-issues)))
                    :total-finished-issues   (:total (first (issue-state/total-finished-issues)))
                    :total-unfinished-issues (:total (first (issue-state/total-unfinished-issues)))})))

(defroutes routes
  (context "/products" []
    (GET  "/" []
          (products))
    (GET  "/:id{[A-Z_0-9]{32}}" [id]
          (a-product id))
    (GET  "/form" []
          (product-form))
    (GET  "/:id{[A-Z_0-9]{32}}/form" [id]
          (product-form id))
    (GET  "/:id{[A-Z_0-9]{32}}/presentation" [id]
          (presentation id))
    (POST "/" {params :params}
          (save-product params))
    (DELETE "/:id{[A-Z_0-9]{32}}" [id] (product/delete id))))
