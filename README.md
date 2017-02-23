# anagram-api #

## Build & Run ##

```sh
$ cd anagram_api
$ sbt "run-main JettyLauncher"
```

This will start up the api.  Open [http://localhost:3000/](http://localhost:3000/) in your browser.

I have set "dockerhost" as the ip off my docker-machine, since I'm not running docker natively.  Please set the config with the correct ip for mysql connector to connect to.

To change the MySQL configs run with `sbt "run-main JettyLauncher" -Dmysql.host=[host]`.  Here is a list of configs:  `mysql.host`, `mysql.host.port`, `mysql.user`, `mysql.password`, `mysql.database`

For unit tests: `sbt test` for end to end tests: go to `givenTests` and run `ruby anagram_test.rb`

Execute `sbt "run-main PopulateDBWithWords"` to set up the population of the mysql tables;