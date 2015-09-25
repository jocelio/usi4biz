(ns usi4biz.routes.web-api
  (:require [clojure.data.json :as json]
            [usi4biz.models.person :as person]
            [compojure.core    :refer [context defroutes GET]]
            [liberator.core    :refer [defresource]]))

(defresource persons []
  :available-media-types ["application/json"]
  :handle-ok (fn [_] (json/write-str (person/find-all-persons))))

(defresource person [id]
  :available-media-types ["application/json"]
  :handle-ok (fn [_] (json/write-str (person/find-person id))))

(defroutes api-routes
  (context "/api/persons" []
    (GET "/"    []   (persons))
    (GET "/:id" [id] (person id))))
