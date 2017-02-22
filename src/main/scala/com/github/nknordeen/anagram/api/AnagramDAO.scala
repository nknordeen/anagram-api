package com.github.nknordeen.anagram.api

import java.sql.{PreparedStatement, Connection}

class AnagramDAO(connection: Connection) {
  val GET_WORD_STATEMENT = connection.prepareStatement(AnagramDAO.SELECT_WORD)
  val INSERT_WORD_STATEMENT = connection.prepareStatement(AnagramDAO.INSERT_WORD)

  def getWord(word: String) = {
    GET_WORD_STATEMENT.setString(1, word)

    AnagramUtils.mySqlResults(GET_WORD_STATEMENT.executeQuery())
  }

  def addWordToDictionary(word: String): Unit = {
    val boundStatement = AnagramDAO.bindInsertStatement(INSERT_WORD_STATEMENT)(word)
    // returns true if the first result is a ResultSet, and result sets aren't returned
    // on inserts, so ok for return type to be Unit
    boundStatement.execute()
  }
}

object AnagramDAO {
  val ALPHABET = 'a' to 'z'
  val ALPHABET_DB_COLUMNS = ALPHABET.mkString(" INT, ") + " INT"
  val COLUMNS_AND_INDEX = ALPHABET.zipWithIndex

  private val SELECT_WORD = "SELECT * FROM indexed_words WHERE word = ?"
  val INSERT_WORD =
    s"""INSERT IGNORE INTO indexed_words (word, ${ALPHABET.mkString(", ")})
        | VALUES (?, ${ALPHABET.map(_ => '?').mkString(", ")})""".stripMargin

  def bindInsertStatement(statement: PreparedStatement)(word: String) = {
    statement.setString(1, word)
    COLUMNS_AND_INDEX.foreach { case (letter, index) =>
      val letterCount = word.count(c => c == letter)
      statement.setInt(index + 2, letterCount)
    }
    statement
  }
}