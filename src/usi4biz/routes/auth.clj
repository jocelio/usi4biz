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
  (:require [compojure.core              :refer [defroutes GET POST]]
            [noir.session                :as session]
            [noir.response               :as response]
            [usi4biz.views.layout        :as layout]
            [tentacles.users             :as users]
            [usi4biz.models.user-account :as user-account]))

(defn login
  ([] (layout/render "login.html" {}))
  ([username password]
   (let [user (user-account/find-by-username username)
         auth (str (:username user) ":" password)
         github-user (users/me {:auth auth})]
     (println github-user)
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
  (GET  "/login"  []        (login))
  (POST "/login"  [username password] (login username password))
  (GET  "/logout" []        (logout)))
