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
  usi4biz.views.layout
  (:require [noir.session       :as session]
            [selmer.parser      :as parser]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]))

(def template-folder "usi4biz/views/templates/")

(defn utf-8-response [html]
  (content-type (response html) "text/html; charset=utf-8"))

(deftype RenderablePage [template params]
  Renderable
  (render [this request]
    (->> (assoc params
                :context (:context request)
                :user (session/get :user))
         (parser/render-file (str template-folder template))
         utf-8-response)))

(defn render [template & [params]]
  (RenderablePage. template params))
