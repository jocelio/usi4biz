(ns usi4biz.utils.calendar)

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
