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
  usi4biz.routes.milestones
  (:require [compojure.core             :refer [defroutes context DELETE GET POST]]
            [liberator.core             :refer [defresource]]
            [clojure.data.json          :as json]
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
        {:milestone (milestone/find-it id)}))

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

(defresource find-by-product [product-id]
  :available-media-types ["application/json"]
  :handle-ok (fn [_]
               (let [milestones (milestone/find-by-product product-id)]
                  (json/write-str milestones))))

(defn milestone-form
  ([]   (milestone-form nil))
  ([id] (selmer/render-file (path-to "milestone_form.html")
          (let [response {:products (product/find-all)
                          :types milestone/types}]
            (if (nil? id)
              response
              (assoc response :milestone (milestone/find-it id)))))))

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
    (DELETE "/:id{[A-Z_0-9]{32}}" [id] (milestone/delete id)))

  (context "/api/milestones" []
    (GET "/" {params :params}
         (find-by-product (:product params)))))
