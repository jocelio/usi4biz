(ns usi4biz.routes.milestones
  (:require [compojure.core             :refer [defroutes context DELETE GET POST]]
            [selmer.parser              :as selmer]
            [usi4biz.models.milestone   :as milestone]
            [usi4biz.models.product     :as product]
            [usi4biz.utils.templates    :refer :all]
            [usi4biz.datasource         :refer [format-date-db format-timestamp-db]]
            [bouncer.core               :as b]))

(defn milestones [params]
  (selmer/render-file (path-to "milestones.html")
    {:products (product/find-all)
     :milestones (milestone/find-by-product (:product params))
     :selected-product (:product params)}))

(defn milestone [id]
      (selmer/render-file (path-to "milestone.html")
        {:milestone (milestone/find id)}))

(defn save-milestone [id product name description, start_sprint, start_sprint_time, due_date, type]
  (let [milestone {:id id
                   :product product
                   :name name
                   :description description
                   :start_sprint (format-timestamp-db (str start_sprint " " start_sprint_time) "dd/MM/yyyy HH:mm")
                   :due_date (format-date-db due_date "dd/MM/yyyy")
                   :type type}]
    (if (b/valid? milestone milestone/validation-rules)
      (selmer/render-file (path-to "milestones.html")
        {:milestone  (milestone/save milestone)
         :milestones (milestone/find-by-product product)
         :products (product/find-all)
         :selected-product (:product milestone)})
      (selmer/render-file (path-to "milestone_form.html")
        {:milestone milestone
         :products (product/find-all)
         :types milestone/types
         :error-product (first (:product (first (b/validate milestone milestone/validation-rules))))}))))

(defn milestone-form
  ([]   (milestone-form nil))
  ([id] (selmer/render-file (path-to "milestone_form.html")
          (let [response {:products (product/find-all)
                          :types milestone/types}]
            (if (nil? id)
              response
              (assoc response :milestone (milestone/find id)))))))

(defroutes routes
  (context "/milestones" []
    (GET  "/" {params :params}
          (milestones params))
    (GET  "/:id{[A-Z_0-9]{32}}" [id]
          (milestone id))
    (GET  "/form" []
          (milestone-form))
    (GET  "/:id{[A-Z_0-9]{32}}/form" [id]
          (milestone-form id))
    (POST "/" [id product name description, start_sprint, start_sprint_time, due_date, type]
          (save-milestone id product name description, start_sprint, start_sprint_time, due_date, type))
    (DELETE "/:id{[A-Z_0-9]{32}}" [id] (milestone/delete id))))
