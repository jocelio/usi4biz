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
  usi4biz.routes.repositories
  (:require [compojure.core             :refer [defroutes context DELETE GET POST]]
            [noir.session               :as session]
            [usi4biz.models.product     :as product]
            [usi4biz.models.issue       :as issue]
            [usi4biz.models.issue-state :as issue-state]
            [usi4biz.models.milestone   :as milestone]
            [usi4biz.models.repository  :as repository]
            [usi4biz.routes.products    :as products]
            [usi4biz.views.layout       :as layout]
            [bouncer.core               :as b]))

(defn save-repository [a-repository]
  (if (b/valid? a-repository repository/validation-rules)
    (products/a-product (:product (repository/save a-repository)))
    (products/a-product (:product a-repository)
                        :error (first (:name (first (b/validate a-repository
                                                                repository/validation-rules)))))))

(defroutes routes
  (context "/repositories" []
    (POST "/" {params :params}
          (save-repository params))
    (DELETE "/:id{[A-Z_0-9]{32}}" [id] (repository/delete id))))
