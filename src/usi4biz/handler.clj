(ns usi4biz.handler
  (:require [compojure.core              :refer :all]
            [compojure.route             :as route]
            [ring.middleware.defaults    :refer [wrap-defaults site-defaults]]
            [usi4biz.datasource          :as ds]
            [usi4biz.routes.chart        :as chart]
            [usi4biz.routes.index        :as index]
            [usi4biz.routes.product      :as product]))

(defn init []
  (println "Usi4Biz is starting...")
  (ds/migrate-db))

(defn destroy []
  (println "Usi4Biz is stopping...")
  (ds/close)
  (println "Datasource closed. Goodbye!"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "<h2>Not Found</h2>"))

(def app
  (-> (routes chart/routes
              index/routes
              product/routes
              app-routes)))
