(ns usi4biz.models.issue
  (:require [hikari-cp.core     :refer :all]
            [clojure.java.jdbc  :as jdbc]
            [usi4biz.datasource :as ds]))

(defrecord issue [id product reference name description milestone assignee effort priority])

(defn find-all-issues []
 (jdbc/with-db-connection [conn {:datasource ds/datasource}]
   (let [rows (jdbc/query conn ["select * from issue"])]
     rows)))

(defn find-by-product [product-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from issue where product = ?" product-id])]
      rows)))

(defn find-by-assignee [person-id]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (let [rows (jdbc/query conn ["select * from issue where assignee = ?" person-id])]
      rows)))

(defn create [issue]
  (jdbc/insert! ds/db-spec :issue (assoc issue :id (ds/unique-id))))

(create (issue. nil
                "DA8B4D129F4849E18799084DC74EF790"
                "EPC-9380"
                "CLONE - Ajouter des types groupements pour des IS, Doctorat"
                nil
                ;"8B56F3FE55114205A36A39D8D2662F93" ; 15.10.1.0
                ;"9A1BB00EEEBA47E8A288DEFA144FAEF2" ; 15.10.2.0
                ;"F56E712F1E7D42ADBBADA3B148E91C41" ; 15.11.1.0
                nil
                ;"12A2194D3B1A4323AD4EE55A1B37760E" ; Axel
                "3BB835DD1F9D4034B6913E26AE84085C" ; Bastien
                ;"16A1C5EA3FE24614A79A98E93EAA2E9A" ; Benoit
                ;"5AC1F6D3980744DD8859345D6388BD74" ; Evase
                ;"3768C92319CD482FB8DA360AFD278BAA" ; Hang
                ;"443124076ECB4A5B94C4FAC1B27B1A48" ; Hildeberto
                ;"485DECA670884B24BC8E31212E6EB845" ; Leila
                ;"88DC33CF26A24DA4B3DC1A7544EF832F" ; Liliane
                ;"2299CB3FD107447EBD00D5467D648AD8" ; Mona
                ;"31A84B88F8904923A43F5B83011E5A69" ; Philippe
                nil
                "IMPORTANT"))
                ;"ESSENCIAL"))

(count (map #(:reference %) (find-all-issues)))
