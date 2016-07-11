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
  usi4biz.handler
  (:require [compojure.core               :refer :all]
            [compojure.route              :as route]
            [compojure.handler            :as handler]
            [noir.util.middleware         :as noir-middleware]
            [usi4biz.datasource           :as ds]
            [usi4biz.routes.auth          :as auth]
            [usi4biz.routes.chart         :as chart]
            [usi4biz.routes.index         :as index]
            [usi4biz.routes.persons       :as persons]
            [usi4biz.routes.products      :as products]
            [usi4biz.routes.repositories  :as repositories]
            [usi4biz.routes.milestones    :as milestones]
            [usi4biz.routes.issues        :as issues]
            [usi4biz.routes.user-accounts :as user-accounts]
            [usi4biz.routes.logs          :as logs]
            [usi4biz.views.layout         :as layout]))

(defn init []
  (println "Usi4Biz is starting...")
  (ds/migrate-db))

(defn destroy []
  (println "Usi4Biz is stopping...")
  (ds/close)
  (println "Datasource closed. Goodbye!"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found (layout/render "not-found.html")))

;(def app
;   (handler/site (routes auth/routes
;                         chart/routes
;                         index/routes
;                         products/routes
;                         milestones/routes
;                         issues/routes
;                         persons/routes
;                         user-accounts/routes
;                         logs/routes
;                         app-routes)))

(def app
  (noir-middleware/app-handler [auth/routes
                                chart/routes
                                index/routes
                                products/routes
                                repositories/routes
                                milestones/routes
                                issues/routes
                                persons/routes
                                user-accounts/routes
                                logs/routes
                                app-routes]))
