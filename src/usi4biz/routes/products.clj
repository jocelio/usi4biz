(ns usi4biz.routes.products
  (:require [compojure.core             :refer [defroutes context GET POST]]
            [selmer.parser              :as selmer]
            [usi4biz.datasource         :as ds]
            [usi4biz.models.product     :as product]
            [usi4biz.models.issue-state :as issue-state]
            [usi4biz.models.milestone   :as milestone]
            [usi4biz.utils.templates    :refer :all]))

(defn products
  ([] (selmer/render-file (path-to "products.html")
        {:products (product/find-all)}))
  ([id]
      (selmer/render-file (path-to "product.html")
        {:product (product/find id)}))
  ([id acronym name description]
      (selmer/render-file (path-to "products.html")
        {:product (product/save {:id id
                                 :acronym acronym
                                 :name name
                                 :description description})
         :products (product/find-all)})))

(defn product-form
  ([]   (selmer/render-file (path-to "product_form.html") {}))
  ([id] (selmer/render-file (path-to "product_form.html") {:product (product/find id)})))

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
  (let [a-product           (first (product/find product-id))
        upcomming-milestone (first (milestone/upcomming-milestone))]
    (selmer/render-file (path-to "presentation.html")
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

(defroutes routes
  (context "/products" []
    (GET  "/" []
          (products))
    (GET  "/:id{[A-Z_0-9]{32}}" [id]
          (products id))
    (GET  "/form" []
          (product-form))
    (GET  "/:id{[A-Z_0-9]{32}}/form" [id]
          (product-form id))
    (GET  "/:id{[A-Z_0-9]{32}}/presentation" [id]
          (presentation id))
    (POST "/" [id acronym name description]
          (products id acronym name description))))
