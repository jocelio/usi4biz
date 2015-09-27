(ns usi4biz.handler
  (:require [compojure.core           :refer :all]
            [compojure.route          :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [usi4biz.datasource       :as ds]
            [usi4biz.routes.web-api   :refer [api-routes]]))

(defn init []
  (println "Usi4Biz is starting...")
  (ds/migrate-db))

(defn destroy []
  (println "Usi4Biz is stopping...")
  (ds/close))

(defroutes app-routes
  (GET "/" []
       (str "<h1>" (slurp "https://api.github.com/zen") "</h1>"))
  (route/not-found "<h2>Not Found</h2>"))

(def app
  (-> (routes api-routes app-routes)))
