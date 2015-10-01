(defproject usi4biz "0.1.0-SNAPSHOT"
  :description "Usi4Biz means usability for business."
  :url "http://usi4biz.com"

  :dependencies [; Core
                 [org.clojure/clojure        "1.7.0" ] ; Core libraries
                 [org.clojure/data.json      "0.2.6" ] ; Json library

                 ; Web
                 [compojure                  "1.3.4" ] ; Routing library
                 [ring-server                "0.4.0" ] ; Default web server
                 [ring/ring-defaults         "0.1.2" ] ;
                 [liberator                  "0.13"  ] ; RESTful library
                 [http-kit                   "2.1.18"] ; High performance web server
                 [selmer                     "0.9.2" ] ; Template engine

                 ; Database
                 [org.clojure/java.jdbc      "0.3.6" ] ; Java JDBC bundle
                 [mysql/mysql-connector-java "5.1.25"] ; MySQL JDBC driver
                 [hikari-cp                  "1.2.4" ] ; Connection pool
                 [joplin.core                "0.2.9" ] ; Database migration
                 [joplin.jdbc                "0.2.9" ] ; Database migration for RDB

                 ; Utils
                 [clj-time                   "0.11.0"] ; Date and time library
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
