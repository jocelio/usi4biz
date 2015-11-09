# usi4biz

User Interaction For Business

## Prerequisites

You will need:
- [Leiningen][1] 1.7.0 or above installed.
- [MySQL][2] 5.6 or above.

[1]: https://github.com/technomancy/leiningen
[2]: http://mysql.com

## Running

### Database configuration

Make sure you have a MySQL database named `usi4biz`, a MySQL username `usi4biz_usr`, with password `secret` with full privileges on the database `usi4biz`.

Copy the file `resources/db-config-example.edn` with the name `resources/db-config.edn`. It is configured to use the credentials above by default. You can change it to reflect your personal credentials if you want. Changes in the file `resources/db-config.edn` won't be tracked by Git.

### Start the application

To start a web server for the application, run:

    lein ring server

## License

GNU GENERAL PUBLIC LICENSE Version 3
Copyright © 2015 Hildeberto Mendonça
