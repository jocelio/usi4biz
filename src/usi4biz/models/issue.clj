(ns usi4biz.models.issue
  (:require [hikari-cp.core             :refer :all]
            [clojure.java.jdbc          :as jdbc]
            [usi4biz.datasource         :as ds]
            [usi4biz.models.issue-state :as is]))

(def assigning-types {:PLANNED            "PLANNED"            ; as result of the sprint meeting
                      :UNPLANNED          "UNPLANNED"          ; something to be patched or undisciplined
                      :UNPLANNED_APPROVED "UNPLANNED_APPROVED" ; unplanned but approved to be included with planned
                      })

(defrecord issue [id product reference name description milestone assignee effort priority, assigning_type])

(defn find-by-reference [reference]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select * from issue where reference = ?" reference])))

(defn create [a-issue]
  (let [issue (assoc a-issue :id (ds/unique-id))]
    (jdbc/insert! ds/db-spec :issue issue)
    issue))

;(create (issue. nil
;                "DA8B4D129F4849E18799084DC74EF790"
;                "EPC-9443"
;                "Problème grave de securité dans le module ETDPortail - quatrième intervention"
;                nil
;                ;"D9E80EF8E9EE43F1B042804DC5EB47D7" ; 15.10.1.1
;                ;"1FB2DD6128DD44AAADA8ECAD4199C88B" ; 15.10.1.2
;                ;"F9179AC300954707AE599D5F94768579" ; 15.10.1.3
;                "F2DE80107F2A40938581B369207A0CDF" ; 15.10.1.4
;                ;"9A1BB00EEEBA47E8A288DEFA144FAEF2" ; 15.10.2.0
;                ;"F56E712F1E7D42ADBBADA3B148E91C41" ; 15.11.1.0
;                ;"28709268554E46BFA9A5B724DAE7E59E" ; 15.11.2.0
;                ;nil ; à affecté
;
;                ;"12A2194D3B1A4323AD4EE55A1B37760E" ; Axel
;                ;"3BB835DD1F9D4034B6913E26AE84085C" ; Bastien
;                ;"16A1C5EA3FE24614A79A98E93EAA2E9A" ; Benoit
;                ;"5AC1F6D3980744DD8859345D6388BD74" ; Evase
;                ;"3768C92319CD482FB8DA360AFD278BAA" ; Hang
;                "443124076ECB4A5B94C4FAC1B27B1A48" ; Hildeberto
;                ;"485DECA670884B24BC8E31212E6EB845" ; Leila
;                ;"88DC33CF26A24DA4B3DC1A7544EF832F" ; Liliane
;                ;"2299CB3FD107447EBD00D5467D648AD8" ; Mona
;                ;"31A84B88F8904923A43F5B83011E5A69" ; Philippe
;                nil
;                ;"IMPORTANT"
;                ;"ESSENCIAL"
;                "BLOCKING"
;                ;"PLANNED"))
;                ;"UNPLANNED"))
;                ;"UNPLANNED_APPROVED"))

(let [ticket (:id (first (find-by-reference "EPC-9107")))]
  (is/create {:issue ticket
              ;:state "CREATED"
              ;:state "ASSIGNED"
              ;:state "FINISHED"
              ;:state "CLOSED"
              :state "CANCELED"
              :set_date "2015-10-19 15:18:00"}
             ))
