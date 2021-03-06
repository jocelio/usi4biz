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
  usi4biz.routes.user-accounts
  (:require [compojure.core              :refer [defroutes context DELETE GET POST]]
            [usi4biz.models.user-account :as user-account]
            [usi4biz.views.layout        :as layout]
            [bouncer.core                :as b]
            [tentacles.repos             :as repos]
            [tentacles.users             :as users]))

(defn user-accounts []
  (layout/render "user_accounts.html"
                 {:user-accounts (user-account/find-all)}))

(defn a-user-account [id]
  (let [user (user-account/find-it id)]
    (layout/render "user_account.html"
                   {:user-account user
                    :user (users/user (:username user))
                    :repos (repos/user-repos (:username user))})))

(defn save-user-account [params]
  (let [user-account params]
    (if (b/valid? user-account user-account/validation-rules)
      (layout/render "user_accounts.html"
                     {:user-account (user-account/save user-account)
                      :user-accounts (user-account/find-all)})
      (layout/render "user_account_form.html"
                     {:user-account user-account
                      :error-name (first (:username (first (b/validate user-account user-account/validation-rules))))}))))

(defn user-account-form
  ([]   (layout/render "user_account_form.html" {}))
  ([id] (layout/render "user_account_form.html" {:user-account (user-account/find-it id)})))

(defroutes routes
  (context "/user_accounts" []
    (GET  "/" []
          (user-accounts))
    (GET  "/:id{[A-Z_0-9]{32}}" [id]
          (a-user-account id))
    (GET  "/form" []
          (user-account-form))
    (GET  "/:id{[A-Z_0-9]{32}}/form" [id]
          (user-account-form id))
    (POST "/" {params :params}
          (save-user-account params))
    (DELETE "/:id{[A-Z_0-9]{32}}" [id] (user-account/delete id)))

  (context "/profile" []
    (GET "/:id{[A-Z_0-9]{32}}" [id]
         (a-user-account id))))
