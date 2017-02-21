package com.github.nknordeen.anagram.api

import org.scalatra._


class AnagramApi(implicit anagramDAO: AnagramDAO) extends ScalatraServlet  {

  get("/") {
    Ok(anagramDAO.getWord("nick"))
  }
}
