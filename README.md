# anagram-api #

## Build & Run ##

```sh
$ cd anagram_api
$ sbt "run-main PopulateDBWithWords"
$ sbt "run-main JettyLauncher"
```

This will start up the api.

I have set "dockerhost" as the default connection host for connecting to the MySQL database.  Please set the config with the correct ip for mysql connector.

To change the MySQL configs run with `sbt "run-main JettyLauncher" -Dmysql.host=[host]`.  Here is a list of configs:  `mysql.host`, `mysql.host.port`, `mysql.user`, `mysql.password`, `mysql.database`

Execute `sbt "run-main PopulateDBWithWords"` to set up the population of the mysql tables.  This is required to create the table in MySQL!

For unit tests: `sbt test` for end to end tests: go to `givenTests` and run `ruby anagram_test.rb`

# Endpoints: #

* `get("/anagrams/:word.json")`: returns a list of all the anagrams of word
* `get("/wordStats")`: returns min/max/median/average word length and count of all words
* `post("/areAnagrams")`:  pass a list of words and returns true or false if all words are anagrams of each other. Example post json request:


    ```
        {
            "words": [...]
        }
    ```


* `post("/words.json")`: adds word(s) to the dictionary (MySQL).  Example post json request:


    ```
        {
            "words": [...]
        }
    ```


* `delete("/words/:word.json")`: deletes a single word from the dictionary
* `delete("/anagrams/:word.json")`: deletes the word passed plus all the words that are anagrams of it.
* `delete("/words.json")`: DANGER:  deletes all words in dictionary.