(ns usi4biz.utils.calendar)

(def months-names ["January" "February" "March" "April" "May" "June" "July" "August" "September" "October" "November" "December"])

(defn month-name [month]
  (if (nil? month)
    ""
    (nth months-names (- month 1))))
