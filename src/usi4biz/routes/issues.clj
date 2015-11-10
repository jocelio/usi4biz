(ns usi4biz.routes.milestones
  (:require [compojure.core             :refer [defroutes context DELETE GET POST]]
            [selmer.parser              :as selmer]
            [usi4biz.models.product     :as product]
            [usi4biz.models.milestone   :as milestone]
            [usi4biz.models.issue       :as issue]
            [usi4biz.utils.templates    :refer :all]
            [usi4biz.datasource         :refer [format-date-db format-timestamp-db]]
            [bouncer.core               :as b]))

(defn issues [params]
  (selmer/render-file (path-to "issues.html")
    {:products (product/find-all)
     :milestones (milestone/find-by-product (:product params))
     :issues (issue/search params)
     :selected-product (:product params)
     :selected-milestone (:milestone params)}))

(defn issue [id]
      (selmer/render-file (path-to "issue.html")
        {:issue (milestone/find id)}))

(defn save-issue [issue]
    (if (b/valid? issue issue/validation-rules)
      (selmer/render-file (path-to "issues.html")
        {:issue  (issue/save issue)
         :milestones (issue/find-by-product product)
         :products (product/find-all)
         :selected-product (:product milestone)
         :selected-milestone (:milestone milestone)})
      (selmer/render-file (path-to "issue_form.html")
        {:issue issue
         :products (product/find-all)
         :assigning-types issue/assigning-types
         :priority-types issue/priority-types}))))

(defn issue-form
  ([]   (issue-form nil))
  ([id] (selmer/render-file (path-to "issue_form.html")
          (let [response {:products (product/find-all)
                          :assigning-types issue/assigning-types
                          :priority-types issue/priority-types}]
            (if (nil? id)
              response
              (assoc response :issue (issue/find id)))))))

(defroutes routes
  (context "/issues" []
    (GET  "/" {params :params}
          (issues params))
    (GET  "/:id{[A-Z_0-9]{32}}" [id]
          (issue id))
    (GET  "/form" []
          (issue-form))
    (GET  "/:id{[A-Z_0-9]{32}}/form" [id]
          (issue-form id))
    (POST "/" {params :params}
          (save-issue params))
    (DELETE "/:id{[A-Z_0-9]{32}}" [id] (issue/delete id))))
