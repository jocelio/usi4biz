(ns usi4biz.routes.issues
  (:require [compojure.core             :refer [defroutes context DELETE GET POST]]
            [selmer.parser              :as selmer]
            [usi4biz.models.product     :as product]
            [usi4biz.models.milestone   :as milestone]
            [usi4biz.models.issue       :as issue]
            [usi4biz.models.issue-state :as issue-state]
            [usi4biz.models.person      :as person]
            [usi4biz.utils.templates    :refer :all]
            [usi4biz.datasource         :refer [format-date-db format-timestamp-db]]
            [bouncer.core               :as b]))

(defn issues [params]
  (selmer/render-file (path-to "issues.html")
    {:products (product/find-all)
     :milestones (milestone/find-by-product (:product params))
     :assignees (person/find-all)
     :issues (issue/search params)
     :params params}))

(defn issue [id]
      (selmer/render-file (path-to "issue.html")
        {:issue (issue/find-it id)
         :issue-states (issue-state/find-by-issue id)}))

(defn save-issue [params]
    (if (b/valid? issue issue/validation-rules)
      (let [issue (dissoc params :state :set_date :set_date_time)
            issue-state (select-keys params [:state :set_date :set_date_time])]
        (selmer/render-file (path-to "issues.html")
          {:issue  (issue/save issue
                               (dissoc (assoc issue-state
                                              :set_date
                                              (format-timestamp-db (str (:set_date issue-state)
                                                                        " "
                                                                        (:set_date_time issue-state))
                                                                   "dd/MM/yyyy HH:mm"))
                                       :set_date_time))
           :milestones (milestone/find-by-product (:product issue))
           :products (product/find-all)
           :assignees (person/find-all)
           :selected-product (:product issue)
           :selected-milestone (:milestone issue)
           :selected-assignee (:assignee issue)})
        (selmer/render-file (path-to "issue_form.html")
          {:issue issue
           :products (product/find-all)
           :milestones (milestone/find-by-product (:product issue))
           :assignees (person/find-all)
           :assigning-types issue/assigning-types
           :priority-types issue/priority-types}))))

(defn issue-form
  ([]   (issue-form nil))
  ([id] (selmer/render-file (path-to "issue_form.html")
          (let [issue (issue/find-it id)
                response {:products (product/find-all)
                          :milestones (milestone/find-by-product (:product issue))
                          :assignees (person/find-all)
                          :assigning-types issue/assigning-types
                          :priority-types issue/priority-types
                          :states issue-state/states
                          :issue-states (issue-state/find-by-issue id)}]
            (if (nil? id)
              response
              (assoc response :issue issue))))))

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
