# anagram-api #

## Build & Run ##

```sh
$ cd anagram_api
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.


I have set "dockerhost" as the ip off my docker-machine, since I'm not running docker natively.  Please set the config with the correct ip for mysql connector to connect to

Execute `sbt "run-main PopulateDBWithWords"` to set up the population of the mysql tables;