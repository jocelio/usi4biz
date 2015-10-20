(ns usi4biz.routes.index
  (:require [compojure.core             :refer [defroutes GET]]
            [selmer.parser              :as selmer]
            [usi4biz.models.product     :as product]))

(defn index []
  (selmer/render-file "usi4biz/views/templates/index.html"
      {:products (product/find-all-products)}))

(defroutes routes
  (GET "/" [] (index)))
