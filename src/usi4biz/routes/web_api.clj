(ns usi4biz.routes.web-api
  (:require [clojure.data.json :as json]
            [usi4biz.models.server :as server]
            [compojure.core    :refer [context defroutes GET]]
            [liberator.core    :refer [defresource]]))

(defresource servers []
  :available-media-types ["application/json"]
  :handle-ok (fn [_] (json/write-str (server/find-all-servers))))

(defresource server [id]
  :available-media-types ["application/json"]
  :handle-ok (fn [_] (json/write-str (server/find-server id))))

(defroutes api-routes
  (context "/api/servers" []
    (GET "/"    []   (servers))
    (GET "/:id" [id] (server id))))
