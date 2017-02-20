package com.github.nknordeen.anagram.api

import java.sql.ResultSet

import scala.collection.mutable.ListBuffer

object AnagramUtils {

  def mySqlResults(rs: ResultSet): List[String] = {
    val c = ListBuffer[String]()
    while (rs.next){
      c.append(rs.getString("word"))
    }
    c.toList
  }


}
