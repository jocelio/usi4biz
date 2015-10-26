(ns usi4biz.models.issue
  (:require [hikari-cp.core             :refer :all]
            [clojure.java.jdbc          :as jdbc]
            [usi4biz.datasource         :as ds]
            [usi4biz.models.issue-state :as iss]))

(def assigning-types {:PLANNED            "PLANNED"            ; as result of the sprint meeting
                      :UNPLANNED          "UNPLANNED"          ; something to be patched or undisciplined
                      :UNPLANNED_APPROVED "UNPLANNED_APPROVED" ; unplanned but approved to be included with planned
                      })

(defrecord issue [id product reference name description milestone assignee effort priority assigning_type])

(defn find-by-reference [reference]
  (jdbc/with-db-connection [conn {:datasource ds/datasource}]
    (jdbc/query conn ["select * from issue where reference = ?" reference])))

(defn create [a-issue]
  (let [issue (assoc a-issue :id (ds/unique-id))]
    (jdbc/insert! ds/db-spec :issue issue)
    issue))

;(comment
(let [id (:id (create (issue. nil
                "DA8B4D129F4849E18799084DC74EF790"
                "EPC-9461"
                "[CORRECTION URGENTE] : Documents à archiver + dossier de l'étudiant - Poids à recupérer dans la LCP du programme type"
                nil ; description
                ;"9A1BB00EEEBA47E8A288DEFA144FAEF2" ; 15.10.2.0
                "E08A644C41C94AB6B7E4B9B1E0EC430C" ; 15.10.2.1
                ;"F56E712F1E7D42ADBBADA3B148E91C41" ; 15.11.1.0
                ;"28709268554E46BFA9A5B724DAE7E59E" ; 15.11.2.0
                ;nil ; à affecté

                ;nil ; personne
                ;"12A2194D3B1A4323AD4EE55A1B37760E" ; Axel
                ;"3BB835DD1F9D4034B6913E26AE84085C" ; Bastien
                ;"16A1C5EA3FE24614A79A98E93EAA2E9A" ; Benoit
                "5AC1F6D3980744DD8859345D6388BD74" ; Evase
                ;"3768C92319CD482FB8DA360AFD278BAA" ; Hang
                ;"443124076ECB4A5B94C4FAC1B27B1A48" ; Hildeberto
                ;"485DECA670884B24BC8E31212E6EB845" ; Leila
                ;"88DC33CF26A24DA4B3DC1A7544EF832F" ; Liliane
                ;"2299CB3FD107447EBD00D5467D648AD8" ; Mona
                ;"31A84B88F8904923A43F5B83011E5A69" ; Philippe
                nil ; effort
                ;"IMPORTANT"
                ;"ESSENCIAL"
                "BLOCKING"
                ;nil ; Planning not defined
                ;"PLANNED"
                "UNPLANNED"
                ;"UNPLANNED_APPROVED"
          )))]
    (iss/create {:issue id :state "CREATED" :set_date "2015-10-26 11:20:00"}))
;)

;(comment
(let [ticket (:id (first (find-by-reference "EPC-9461")))]
  (iss/create {:issue ticket
               ;:state "ASSIGNED"
               ;:state "FINISHED"
               :state "CLOSED"
               ;:state "CANCELED"
               :set_date "2015-10-26 23:36:00"}))
;)
