(ns usi4biz.routes.home
  (:require [compojure.core :refer :all]
            [usi4biz.views.layout :as layout]))

(defn home []
  (layout/common [:h1 "Hello World!"]))

(defroutes home-routes
  (GET "/" [] (home)))
