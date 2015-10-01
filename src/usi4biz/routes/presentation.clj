(ns usi4biz.routes.presentation
  (:require [compojure.core         :refer [defroutes GET]]
            [selmer.parser          :as selmer]
            [usi4biz.models.product :as product]))

(defn index [acronym]
  (let [a-product (first (product/find-by-acronym acronym))]
    (selmer/render-file "usi4biz/views/templates/index.html"
      {:product (:acronym a-product)
       :author "Hildeberto Mendonça, Ph.D."})))

(defroutes routes
  (GET "/:acronym" [acronym] (index acronym)))
