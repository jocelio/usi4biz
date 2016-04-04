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
  usi4biz.models.user-account
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]
            [bouncer.validators :as v]
            [tentacles.users    :as users]
            [noir.session       :as session]))

(def validation-rules {:username v/required})

(defn find-all []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from user_account"])]
     rows)))

(defn find-it [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (first (jdbc/query conn ["select * from user_account where id = ?" id]))))

(defn find-by-username [username]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (first (jdbc/query conn ["select * from user_account where username = ?" username]))))

(defn find-profile []
  (users/me {:auth (session/get :auth)}))

(defn save [a-user-account]
  (if (empty? (:id a-user-account))
    (let [user-account (assoc a-user-account :id (ds/unique-id))]
      (jdbc/insert! ds/db-spec :user_account user-account)
      user-account)
    (let [user-account a-user-account]
      (jdbc/update! ds/db-spec :user_account user-account ["id = ?" (:id user-account)])
      user-account)))

(defn delete [id]
  (jdbc/delete! ds/db-spec :user_account ["id = ?" id]) id)
