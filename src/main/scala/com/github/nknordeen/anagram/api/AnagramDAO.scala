package com.github.nknordeen.anagram.api

import java.sql.{PreparedStatement, Connection}

class AnagramDAO(connection: Connection) {
  val GET_WORD_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_WORD)
  val INSERT_WORD_STATEMENT = connection.prepareStatement(AnagramDAO.INSERT_WORD)
  val DELETE_WORD_STATEMENT = connection.prepareStatement(AnagramDAO.DELETE_WORD)
  val SELECT_ANAGRAM_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_ANAGRAM)

  def getWord(word: String) = {
    GET_WORD_STATEMENT.setString(1, word)

    AnagramUtils.mySqlResults(GET_WORD_STATEMENT.executeQuery())
  }

  def addWordToDictionary(word: String): Unit = {
    val boundStatement = AnagramDAO.bindInsertStatement(INSERT_WORD_STATEMENT)(word.toLowerCase())
    // returns true if the first result is a ResultSet, and result sets aren't returned
    // on inserts, so ok for return type to be Unit
    boundStatement.execute()
  }

  def getAnagrams(word: String, size: Option[Int]): Anagrams = {
    val lowerCase = word.toLowerCase()
    AnagramDAO.ALPHABET.zipWithIndex.foreach { case (letter, index) =>
      val count = lowerCase.count(c => c == letter)
      SELECT_ANAGRAM_STATEMENT.setInt(index + 1, count)
    }
    val anagrams = if (size.isDefined) {
      AnagramUtils.mySqlResults(SELECT_ANAGRAM_STATEMENT.executeQuery()).take(size.get)
    } else {
      AnagramUtils.mySqlResults(SELECT_ANAGRAM_STATEMENT.executeQuery())
    }
    new Anagrams(anagrams.filter(anagram => anagram.toLowerCase() != lowerCase))
  }

  def deleteWord(word: String): Unit = {
    DELETE_WORD_STATEMENT.setString(1, word)
    DELETE_WORD_STATEMENT.execute()
  }

  def deleteAllWords(): Unit = {
    connection.prepareStatement(AnagramDAO.DELETE_ALL_WORDS).execute()
  }
}

object AnagramDAO {
  val ALPHABET = 'a' to 'z'
  val ALPHABET_DB_COLUMNS = ALPHABET.mkString(" INT, ") + " INT"
  val COLUMNS_AND_INDEX = ALPHABET.zipWithIndex

  val SELECT_WORD = "SELECT * FROM indexed_words WHERE word = ?"

  val INSERT_WORD =
    s"""INSERT IGNORE INTO indexed_words (word, ${ALPHABET.mkString(", ")})
        | VALUES (?, ${ALPHABET.map(_ => '?').mkString(", ")})""".stripMargin

  val SELECT_ANAGRAM = s"SELECT * FROM indexed_words WHERE ${ALPHABET.mkString(" = ? AND ")} = ?"

  val DELETE_WORD = "DELETE FROM indexed_words WHERE word = ?"

  val DELETE_ALL_WORDS = "TRUNCATE indexed_words"

  def bindInsertStatement(statement: PreparedStatement)(word: String) = {
    statement.setString(1, word)
    COLUMNS_AND_INDEX.foreach { case (letter, index) =>
      val letterCount = word.count(c => c == letter)
      statement.setInt(index + 2, letterCount)
    }
    statement
  }
}