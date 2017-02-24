package com.github.nknordeen.anagram.api

import java.sql.{ResultSet, PreparedStatement, Connection}

class AnagramDAO(connection: Connection) {
  val INSERT_WORD_STATEMENT = connection.prepareStatement(AnagramDAO.INSERT_WORD)
  val DELETE_WORD_STATEMENT = connection.prepareStatement(AnagramDAO.DELETE_WORD)
  val SELECT_ANAGRAM_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_ANAGRAM)
  val DELETE_WORD_AND_ANAGRAM_STATEMENT = connection.prepareStatement(AnagramDAO.DELETE_WORD_AND_ANAGRAMS)
  val SELECT_AVERAGE_LENGTH_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_AVERAGE_LENGTH)
  val SELECT_MIN_LENGTH_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_MIN_LENGTH)
  val SELECT_MAX_LENGTH_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_MAX_LENGTH)
  val SELECT_ALL_WORDS_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_ALL_WORDS)


  /**
    * returns a list of anagrams made up of the letters in `word`.  It only returns single words, as a list, and it also
    * excludes the word passed from the list returned.
    *
    * @param word - letters are used to find the other anagrams
    * @param size - number of entries to return
    * @return
    */
  def getAnagrams(word: String, size: Option[Int]): Anagrams = {
    val lowerCase = word.toLowerCase()
    AnagramDAO.COLUMNS_AND_INDEX.foreach { case (letter, index) =>
      val count = lowerCase.count(c => c == letter)
      SELECT_ANAGRAM_STATEMENT.setInt(index + 1, count)
    }
    val anagrams = if (size.isDefined) {
      AnagramUtils.mySqlResults(SELECT_ANAGRAM_STATEMENT.executeQuery())(AnagramDAO.getString("word")).take(size.get)
    } else {
      AnagramUtils.mySqlResults(SELECT_ANAGRAM_STATEMENT.executeQuery())(AnagramDAO.getString("word"))
    }
    new Anagrams(anagrams.filter(anagram => anagram.toLowerCase() != lowerCase))
  }

  /**
    * Finds and returns basic statistics off words in the dictionary.  It returns the minimum, maximum, average, and median
    * word size
    * @return - Stats object with min/max/avg/median
    */
  def getDictionaryStats: DictionaryStats = {
    val sortedByLength = AnagramUtils.mySqlResults(SELECT_ALL_WORDS_STATEMENT.executeQuery())(AnagramDAO.getString("word"))
      .sortWith((s1, s2) => s1.length < s2.length)
    val medianLength = if (sortedByLength.isEmpty) {
      0
    } else if (sortedByLength.length % 2 == 1) {
      sortedByLength(sortedByLength.length / 2).length
    } else {
      val l1 = sortedByLength(sortedByLength.length / 2).length
      val l2 = sortedByLength((sortedByLength.length / 2) - 1).length
      (l1 + l2) / 2
    }
    // if values are null, getInt returns 0
    val count = sortedByLength.length
    val maxLength = AnagramUtils.mySqlResults(SELECT_MAX_LENGTH_STATEMENT.executeQuery())(AnagramDAO.getInt("max")).head
    val minLength = AnagramUtils.mySqlResults(SELECT_MIN_LENGTH_STATEMENT.executeQuery())(AnagramDAO.getInt("min")).head
    val avgLength = AnagramUtils.mySqlResults(SELECT_AVERAGE_LENGTH_STATEMENT.executeQuery())(AnagramDAO.getDouble("avg")).head
    DictionaryStats(count, minLength, maxLength, medianLength, avgLength)
  }

  /**
    * Adds word to MySQL db
    * @param word - word to add
    */
  def addWordToDictionary(word: String): Unit = {
    val boundStatement = AnagramDAO.bindInsertStatement(INSERT_WORD_STATEMENT)(word.toLowerCase())
    // returns true if the first result is a ResultSet, and result sets aren't returned
    // on inserts, so ok for return type to be Unit
    boundStatement.execute()
  }

  /**
    * Removes a word from MySQL db
    * @param word - word to remove
    */
  def deleteWord(word: String): Unit = {
    DELETE_WORD_STATEMENT.setString(1, word)
    DELETE_WORD_STATEMENT.execute()
  }

  /**
    * Removes word and all of it's anagrams from the dictionary.  For instance if read it removes read, dear, and dare
    * @param word - word to remove
    */
  def deleteWordAndAnagrams(word: String): Unit = {
    val lowerCase = word.toLowerCase()
    AnagramDAO.COLUMNS_AND_INDEX.foreach { case (letter, index) =>
      val count = lowerCase.count(c => c == letter)
      DELETE_WORD_AND_ANAGRAM_STATEMENT.setInt(index + 1, count)
    }
    DELETE_WORD_AND_ANAGRAM_STATEMENT.execute()
  }

  /**
    * Truncates the MySQL table
    */
  def deleteAllWords(): Unit = {
    connection.prepareStatement(AnagramDAO.DELETE_ALL_WORDS).execute()
  }
}

object AnagramDAO {
  val ALPHABET = 'a' to 'z'
  val ALPHABET_DB_COLUMNS = ALPHABET.mkString(" INT, ") + " INT"
  val COLUMNS_AND_INDEX = ALPHABET.zipWithIndex

  val INSERT_WORD =
    s"""INSERT IGNORE INTO indexed_words (word, ${ALPHABET.mkString(", ")})
        | VALUES (?, ${ALPHABET.map(_ => '?').mkString(", ")})""".stripMargin

  val SELECT_ANAGRAM = s"SELECT * FROM indexed_words WHERE ${ALPHABET.mkString(" = ? AND ")} = ?"

  val SELECT_AVERAGE_LENGTH = "SELECT AVG(CHAR_LENGTH(word)) as avg FROM indexed_words;"

  val SELECT_MIN_LENGTH = "SELECT MIN(CHAR_LENGTH(word)) as min FROM indexed_words;"

  val SELECT_MAX_LENGTH = "SELECT MAX(CHAR_LENGTH(word)) as max FROM indexed_words;"

  val SELECT_ALL_WORDS = "SELECT word FROM indexed_words;"

  val DELETE_WORD = "DELETE FROM indexed_words WHERE word = ?"

  val DELETE_WORD_AND_ANAGRAMS = s"DELETE FROM indexed_words WHERE ${ALPHABET.mkString(" = ? AND ")} = ?"

  val DELETE_ALL_WORDS = "TRUNCATE indexed_words"

  def bindInsertStatement(statement: PreparedStatement)(word: String) = {
    statement.setString(1, word)
    COLUMNS_AND_INDEX.foreach { case (letter, index) =>
      val letterCount = word.count(c => c == letter)
      statement.setInt(index + 2, letterCount)
    }
    statement
  }

  private def getString(columnName: String)(rs: ResultSet) = {
    rs.getString(columnName)
  }

  private def getInt(columnName: String)(rs: ResultSet) = {
    rs.getInt(columnName)
  }

  private def getDouble(columnName: String)(rs: ResultSet) = {
    rs.getDouble(columnName)
  }
}