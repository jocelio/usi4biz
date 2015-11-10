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
  usi4biz.utils.calendar)

(def months-names ["January" "February" "March" "April" "May" "June"
                   "July" "August" "September" "October" "November" "December"])

(defn month-name [month]
  (if (nil? month)
    ""
    (nth months-names (- month 1))))

(defn to-date [str-date format]
  (println (str format ": " str-date))
  (.parse (java.text.SimpleDateFormat. format) str-date))

(defn to-string [date format]
  (.format (java.text.SimpleDateFormat. format) date))
