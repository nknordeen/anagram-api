package com.github.nknordeen.anagram.api

import java.sql.ResultSet

import scala.collection.mutable.ListBuffer

object AnagramUtils {

  val NON_ALPHABETIC_REGEX = "[^A-Za-z]".r

  def mySqlResults[T](rs: ResultSet)(constructor: (ResultSet) => T): List[T] = {
    val c = ListBuffer[T]()
    while (rs.next){
      c.append(constructor(rs))
    }
    c.toList
  }

  def isValidWord(word: String): Boolean = {
    NON_ALPHABETIC_REGEX.findFirstIn(word).isEmpty
  }

  /**
    * if there are spaces between letters we will assume that those are multiple words,
    * after extracting the extra words, remove the spaces
    * @param word - removes spaces
    * @return possible words that have been extracted, and no spaces
    */
  def removeSpaces(word: String): Seq[String] = {
    word.split(" ").map(_.replace(" ", "")).filter(_.nonEmpty)
  }
}
