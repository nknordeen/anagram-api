package com.github.nknordeen.anagram.api

import org.json4s.ext.JavaTypesSerializers
import org.json4s.{MappingException, NoTypeHints}
import org.json4s.native.Serialization
import org.scalatra._
import org.scalatra.json.NativeJsonSupport


class AnagramApi(implicit anagramDAO: AnagramDAO) extends ScalatraServlet with NativeJsonSupport {

  implicit val jsonFormats = Serialization.formats(NoTypeHints) ++ JavaTypesSerializers.all

  private val PAGE_LIMIT = "limit"

  before() {
    contentType = formats("json")
  }

  get("/anagrams/:word.json") {
    val word = params("word")
    val limit = params.get(PAGE_LIMIT).map(_.toInt)
    if (AnagramUtils.isValidWord(word)) {
      Ok(body = anagramDAO.getAnagrams(word, limit))
    } else {
      BadRequest(reason = s"Invalid word: $word")
    }
  }

  /**
    * Return 400 if there are invalid words, but still inserts the valid words received.
    */
  post("/words.json") {
    try {
      val words = parsedBody.extract[Words]
      val allWords = words.words
        .flatMap(AnagramUtils.removeSpaces)
      val validWords = allWords
        .filter(AnagramUtils.isValidWord)
      val invalidWords = allWords
        .filter(word => !AnagramUtils.isValidWord(word))
      validWords.foreach(anagramDAO.addWordToDictionary)
      val wordsNotCreated = invalidWords.mkString(", ")

      if (words.words.isEmpty) {
        BadRequest(reason = "Unable to find words to insert")
      } else if (invalidWords.isEmpty) {
        Created()
      } else {
        BadRequest(reason = s"Words: [ $wordsNotCreated ] were not valid words.  The rest were created")
      }
    } catch {
      case mape: MappingException => BadRequest(reason = mape.msg)
    }
  }

  delete("/words/:word.json") {
    val word = params("word")
    if (AnagramUtils.isValidWord(word)) {
      anagramDAO.deleteWord(word)
      Ok()
    } else {
      BadRequest(reason = s"Invalid word: $word")
    }
  }

  delete("/words.json") {
    anagramDAO.deleteAllWords()
    NoContent()
  }
}
