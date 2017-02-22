package com.github.nknordeen.anagram.api

import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.scalatra._
import org.scalatra.json.NativeJsonSupport


class AnagramApi(implicit anagramDAO: AnagramDAO) extends ScalatraServlet with NativeJsonSupport {

  implicit val jsonFormats = Serialization.formats(NoTypeHints)

  get("/:word") {
    val word = params("word")
    Ok(anagramDAO.getWord(word))
  }

  post("/") {
    val words = parsedBody.extract[List[String]]
    val allWords = words
      .flatMap(AnagramUtils.removeSpaces)
    val validWords = allWords
      .filter(AnagramUtils.isValidWord)
    val invalidWords = allWords
      .filter(word => !AnagramUtils.isValidWord(word))
    validWords.foreach(anagramDAO.addWordToDictionary)
    val wordsNotCreated = invalidWords.mkString(", ")
    if (invalidWords.isEmpty) {
      Created()
    } else {
      BadRequest(reason = s"Words: [ $wordsNotCreated ] were not valid words.  The rest were created")
    }
  }
}
