(ns usi4biz.models.person
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord person [id first_name last_name email user_account])

(defn find-all-persons []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from person"])]
     rows)))

(defn find-person [id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from person where id = ?" id])]
      rows)))

(defn create [person]
  (jdbc/insert! ds/db-spec :person (assoc person :id (ds/unique-id))))

(create (person. nil
                 "Liliane"
                 "Lambaux"
                 "liliane.lambaux@uclouvain.be"
                 nil))
