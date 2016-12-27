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
  usi4biz.routes.auth
  (:require [bouncer.core                :as b]
            [compojure.core              :refer [defroutes GET POST]]
            [noir.session                :as session]
            [noir.response               :as response]
            [usi4biz.views.layout        :as layout]
            [tentacles.users             :as users]
            [usi4biz.models.person       :as person]
            [usi4biz.models.user-account :as user-account]))

(defn signup
  ([] (layout/render "signup.html" {}))
  ([params]
    (let [user-account (select-keys params [:username])
          person       (select-keys params [:first_name :last_name :email])]
      (if (and (b/valid? user-account user-account/validation-rules)
               (b/valid? person person/validation-rules))
        (response/redirect "/login")
        (layout/render "signup.html"
                       {:user-account user-account
                        :person       person})))))

(defn login
  ([] (layout/render "login.html" {}))
  ([username password]
   (let [user (user-account/find-by-username username)
         auth (str (:username user) ":" password)
         github-user (users/me {:auth auth})]
     (if (:status github-user)
       (response/redirect "/login")
       (do
         (session/put! :user user)
         (session/put! :auth auth)
         (response/redirect "/"))))))

(defn logout []
  (session/clear!)
  (response/redirect "/"))

(defroutes routes
  (GET  "/signup" []                  (signup))
  (POST "/signup" {params :params}    (signup params))
  (GET  "/login"  []                  (login))
  (POST "/login"  [username password] (login username password))
  (GET  "/logout" []                  (logout)))
