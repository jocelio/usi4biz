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

(defproject usi4biz "0.2.0-SNAPSHOT"
  :description "User Interaction For Business"
  :url "http://usi4biz.com"

  :dependencies [; Core
                 [org.clojure/clojure        "1.7.0" ] ; Core libraries
                 [org.clojure/data.json      "0.2.6" ] ; Json library

                 ; Web
                 [compojure                  "1.4.0" ] ; Routing library
                 [ring-server                "0.4.0" ] ; Default web server
                 [ring/ring-defaults         "0.1.2" ] ; HTTP middleware
                 [liberator                  "0.13"  ] ; RESTful library
                 [http-kit                   "2.1.18"] ; High performance web server
                 [selmer                     "1.0.0" ] ; Template engine

                 ; Database
                 [org.clojure/java.jdbc      "0.3.6" ] ; Java JDBC bundle
                 [mysql/mysql-connector-java "5.1.25"] ; MySQL JDBC driver
                 [hikari-cp                  "1.2.4" ] ; Connection pool
                 [joplin.core                "0.2.9" ] ; Database migration
                 [joplin.jdbc                "0.2.9" ] ; Database migration for RDB

                 ; Utils
                 [bouncer                    "0.3.3" ] ; Validation library
                 [org.slf4j/slf4j-log4j12    "1.7.1" ] ; Logging library
                 [log4j/log4j                "1.2.17"  ; Logging library
                  :exclusions [javax.mail/mail
                               javax.jms/jms
                               com.sun.jmdk/jmxtools
                               com.sun.jmx/jmxri]    ]]

  :plugins [[lein-ring "0.8.13"]]

  :ring {:init    usi4biz.handler/init
         :handler usi4biz.handler/app
         :destroy usi4biz.handler/destroy}

  :profiles {:uberjar {:aot :all}
             :production {:ring {:open-browser? false,
                                 :stacktraces? false,
                                 :auto-reload? false}}
             :dev {:dependencies [[ring-mock       "0.1.5"]
                                  [ring/ring-devel "1.3.1"]]}})
