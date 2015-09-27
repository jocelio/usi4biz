Hildeberto Mendonça
Université catholique de Louvain, Belgium

GIT + Clojure

# Part 1

## Install Leiningen

    $ sudo mkdir /Applications/clojure

    $ cd /Applications/clojure
    $ sudo curl -O https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
    $ sudo chmod +x lein

## Configure Leiningen

    $ cd /etc/paths.d/
    $ echo "/Applications/clojure" | sudo tee /etc/paths.d/clojure.lein

## Demo of the REPL

    $ lein repl
    => (println "Hello World!")
    => (+ 2 4)
    => (def person "John")
    => (format "Hello %s !" person)
    => (range 1 6)
    => (defn exp [base expoent] (reduce * (repeat exponent base)))

## Clojure Data Structures

### Vector

    [1 2 3 4 5]
    ["a" "b" "c"]
    [6 "sete" 8 "nove" 10]

### List

    '(1 2 3 4 5)

### Map

    {:a "a" :b "b" :c "c"}

### Set

    #{:a :b :c}

## Clojure functional programming

    => (map (fn [a] (if (zero? (rem a 2)) "par" "impar")) [1 2 4 5 7 9 10 12])
    => (filter (fn [a] (if (zero? (rem a 2)) true false)) [1 2 3 4 5])
    => (reduce (fn [a b] (* a b)) [1 2 3 4 5])
    => (reduce (fn [a b] (* a b)) (range 1 6))
    => (reduce #(* %1 %2) (range 1 6))

# Part 2

## Creation of the project

    $ lein new compojure followinsteps
    $ cd followinsteps
    $ lein ring server

## Initialise the Git repository

    $ git init

## Add the files to the repository

    $ git add .

## Make the first commit to master

    $ git commit -m "First commit"

    $ git config --global user.name "John Smith"
    $ git config --global user.email "john.smith@gmail.com"

# Part 3

## External dependencies

### Database

    $ mysql -u root -p
    mysql> create database fa7;
    mysql> create user 'fa7_user'@'localhost' identified by ’fa7_senha’;
    mysql> use fa7;
    mysql> grant all privileges on fa7.* to 'fa7_user'@'localhost';
    mysql> flush privileges;

## Edit `project.clj` to include pending dependencies

    :dependencies [; Core
                  [org.clojure/clojure        "1.7.0" ] ; Core libraries
                  [org.clojure/data.json      "0.2.6" ] ; Json library

                  ; Web
                  [compojure                  "1.3.4" ] ; Routing library
                  [ring-server                "0.4.0" ] ; Default web server
                  [ring/ring-defaults         "0.1.2" ] ;
                  [liberator                  "0.13"  ] ; RESTful library
                  [http-kit                   "2.1.18"] ; High performance web server

                  ; Database
                  [org.clojure/java.jdbc      "0.3.6" ] ; Java JDBC bundle
                  [mysql/mysql-connector-java "5.1.25"] ; MySQL JDBC driver
                  [hikari-cp                  "1.2.4" ] ; Connection pool
                  [joplin.core                "0.2.9" ] ; Database migration
                  [joplin.jdbc                "0.2.9" ] ; Database migration for RDB

                  ; Utils
                  [org.slf4j/slf4j-log4j12    "1.7.1" ] ; Logging library
                  [log4j/log4j                "1.2.17"  ; Logging library
                   :exclusions [javax.mail/mail
                                javax.jms/jms
                                com.sun.jmdk/jmxtools
                                com.sun.jmx/jmxri]    ]]

    :plugins [[lein-ring "0.8.13"]]

    :ring {:handler fa7.handler/app}

    :profiles {:uberjar {:aot :all}
               :production {:ring {:open-browser? false,
                                   :stacktraces? false,
                                   :auto-reload? false}}
               :dev {:dependencies [[ring-mock       "0.1.5"]
                                    [ring/ring-devel "1.3.1"]]}})

## Configure dependencies

### Database Connection

#### src/fa7/datasource.clj

    (ns fa7.datasource
      (:require [clojure.java.jdbc    :as jdbc]))

    (defn create-db-spec [& {:keys [classname subprotocol subname user password]
                             :or {classname   "com.mysql.jdbc.Driver"
                              subprotocol "mysql"
                              subname     "//localhost:3306/fa7"
                              user        "fa7_user"
                              password    "fa7_senha"}}]
      (zipmap [:classname :subprotocol :subname :user :password]
              [classname  subprotocol  subname  user  password]))

    (def db-spec (create-db-spec))

    (jdbc/query db-spec ["select * from test"])

### Database connection pool

#### resources/db-config.edn

    {:auto-commit        true
     :read-only          false
     :connection-timeout 30000
     :validation-timeout 5000
     :idle-timeout       600000
     :max-lifetime       1800000
     :minimum-idle       10
     :maximum-pool-size  10
     :pool-name          "db-pool"
     :driver             "com.mysql.jdbc.Driver"
     :adapter            "mysql"
     :database-name      "fa7"
     :username           "fa7_user"
     :password           "fa7_senha"
     :server-name        "localhost"
     :port-number        3306}

#### src/fa7/datasource.clj

    (ns fa7.datasource
      (:require [clojure.java.jdbc    :as jdbc]
                [clojure.edn          :as edn]
                [hikari-cp.core       :refer :all]))

    (defn db-config []
      (with-open [in (java.io.PushbackReader.
                       (clojure.java.io/reader "resources/db-config.edn"))]
        (edn/read in)))

    (def datasource
      (make-datasource (db-config)))

    (defn close []
      (close-datasource datasource))

    (jdbc/with-db-connection [conn {:datasource datasource}]
        (jdbc/query conn ["select * from test"]))

### Database migration

    (ns fa7.datasource
      (:require [clojure.java.jdbc    :as jdbc]
                [clojure.edn          :as edn]
                [hikari-cp.core       :refer :all]
                [joplin.core          :as joplin]
                [joplin.jdbc.database]))

    ...

    (def joplin-target
      {:db {:type :sql
            :url (str "jdbc:" (:subprotocol db-spec) ":" (:subname  db-spec)
                      "?user=" (:user db-spec) "&password=" (:password db-spec))}
            :migrator "resources/migrators/sql"})

    (defn migrate-db []
      (joplin/migrate-db joplin-target))

    (defn unique-id []
      (.replace (.toUpperCase (str (java.util.UUID/randomUUID))) "-" ""))

## Tagging

    git tag -a v0.1.0 -m "v0.1.0: Project created and configured."

# Part 4

## Create a branch for dev, test and qa

    $ git branch dev
    $ git branch test
    $ git branch qa
    $ git checkout dev

## Create an issue in the issue tracking

## Create a branch from dev to the issue
## Develop the new feature using the branch
## Test new feature
## Commit to the branch
## Merge back to dev
## Merge to test
## Merge to qa
## Merge to master

# Part 5

## Create branch from qa
## Develop fix
## Commit to the branch
## Merge back to qa
## Merge to master

# Part 6

## New branch from dev
## Liberator CRUD
### Create + Commit
### Read + Commit
### Update + Commit
### Delete + Commit
## Merge back to dev

# Part 7

## Git problem solving
### `add`

    $ git reset HEAD

### `commit`

    $ git commit --amend -m "New commit message."

    $ git reset --soft HEAD^
    $ git commit -c ORIG_HEAD

    $ git reset --hard HEAD^

## Contributing to open source projects

    // Only once
    $ git remote add upstream https://github.com/htmfilho/fa7.git

    // Every time
    $ git fetch upstream
    $ git checkout master
    $ git merge upstream/master
    $ git push origin master

## The revival of the analysts
