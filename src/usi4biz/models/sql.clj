(defn from [entities]
  entities)

(defn select [fields from]
  (str "select "
       (reduce #(str (name %1) " " (name %2)) fields)
       (from)))

(select [:id :name :description] from)
