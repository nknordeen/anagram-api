package com.github.nknordeen.anagram.api

import java.sql.Connection

class AnagramDAO(connection: Connection) {

  def getWord(word: String) = {
    val statement = connection.prepareStatement(AnagramDAO.SELECT_WORD)
    statement.setString(1, word)

    AnagramUtils.mySqlResults(statement.executeQuery())
  }
}

object AnagramDAO {
  private val SELECT_WORD = "SELECT * FROM words WHERE word = ?"
}