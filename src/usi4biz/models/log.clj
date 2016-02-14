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
  usi4biz.models.log
  (:require [clojure.java.io :as io]))

(defn find-all []
  (let [directory (io/file "log")
        files (file-seq directory)]
     (sort-by :last-modified (map #(hash-map :file-name     (.getName %)
                                             :last-modified (.lastModified %)
                                             :length (/ (.length %) 1000.0))
                                  (rest files)))))

(defn path-to [log-file]
  (str "log/" log-file))

(defn find-it [log-file]
  (let [file (io/file (path-to log-file))]
    (hash-map :file-name (.getName file)
              :path (.getAbsolutePath file)
              :last-modified (.lastModified file)
              :length (/ (.length file) 1000.0)
              :content (slurp file))))

(defn delete [log-file]
  (io/delete-file (path-to log-file))
  log-file)
