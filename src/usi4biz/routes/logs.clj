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
  usi4biz.routes.logs
  (:require [compojure.core          :refer [defroutes context DELETE GET POST]]
            [selmer.parser           :as selmer]
            [usi4biz.models.log      :as log]
            [usi4biz.utils.templates :refer :all]
            [bouncer.core            :as b]))

(defn logs
  ([] (selmer/render-file (path-to "logs.html")
        {:logs (log/find-all)}))
  ([log-file]
      (selmer/render-file (path-to "log.html")
        {:log (log/find-it log-file)})))

(defroutes routes
  (context "/logs" []
    (GET  "/" []
          (logs))
    (GET  "/:log-file" [log-file]
          (logs log-file))
    (DELETE "/:log-file" [log-file] (log/delete log-file))))
