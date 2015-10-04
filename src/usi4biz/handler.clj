(ns usi4biz.handler
  (:require [compojure.core              :refer :all]
            [compojure.route             :as route]
            [ring.middleware.defaults    :refer [wrap-defaults site-defaults]]
            [usi4biz.datasource          :as ds]
            [usi4biz.routes.presentation :as presentation]
            [usi4biz.routes.chart        :as chart]))

(defn init []
  (println "Usi4Biz is starting...")
  (ds/migrate-db))

(defn destroy []
  (println "Usi4Biz is stopping...")
  (ds/close))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "<h2>Not Found</h2>"))

(def app
  (-> (routes presentation/routes chart/routes app-routes)))
