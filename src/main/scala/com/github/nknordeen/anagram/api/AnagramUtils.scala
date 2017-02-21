package com.github.nknordeen.anagram.api

import java.sql.{Connection, ResultSet}

import scala.collection.mutable.ListBuffer
import scala.io.Source

object AnagramUtils {

  def mySqlResults(rs: ResultSet): List[String] = {
    val c = ListBuffer[String]()
    while (rs.next){
      c.append(rs.getString("word"))
    }
    c.toList
  }

  def importDataToMySQL(connection: Connection): Unit = {
    // we don't care if the word is already there, so failing to insert is fine
    val insertWord = "INSERT IGNORE INTO words (word) VALUES (?)"
    val statement = connection.prepareStatement(insertWord)
    Source.fromFile("dictionary.txt")
      .getLines()
      .foreach(word => {
//        statement.setString(1, word)
//        statement.execute()
      })

  }
}
