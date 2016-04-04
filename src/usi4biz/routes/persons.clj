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
  usi4biz.routes.persons
  (:require [compojure.core          :refer [defroutes context DELETE GET POST]]
            [usi4biz.models.person   :as person]
            [usi4biz.views.layout    :as layout]
            [bouncer.core            :as b]
            [tentacles.issues        :as issues]))

(defn persons []
  (layout/render "persons.html"
                 {:persons (person/find-all)}))

(defn a-person [id]
  (let [person (person/find-it id)]
    (layout/render "person.html"
                   {:person person
                    :issues (issues/issues "uclouvain" "osis-louvain")})))

(defn save-person [params]
  (let [person params]
    (if (b/valid? person person/validation-rules)
      (layout/render "persons.html"
                     {:person (person/save person)
                      :persons (person/find-all)})
      (layout/render "person_form.html"
                     {:person person}))))

(defn person-form
  ([]   (layout/render "person_form.html" {}))
  ([id] (layout/render "person_form.html" {:person (person/find-it id)})))

(defroutes routes
  (context "/persons" []
    (GET  "/" []
          (persons))
    (GET  "/:id{[A-Z_0-9]{32}}" [id]
          (a-person id))
    (GET  "/form" []
          (person-form))
    (GET  "/:id{[A-Z_0-9]{32}}/form" [id]
          (person-form id))
    (POST "/" {params :params}
          (save-person params))
    (DELETE "/:id{[A-Z_0-9]{32}}" [id] (person/delete id))))
