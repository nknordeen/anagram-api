package com.github.nknordeen.anagram.api

import java.sql.ResultSet

import org.scalatra._


class AnagramApi(implicit anagramDAO: AnagramDAO) extends ScalatraServlet  {

  get("/") {
    Ok(anagramDAO.getWord("nick"))
  }
}
