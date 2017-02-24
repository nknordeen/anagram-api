package com.github.nknordeen.anagram.api

case class Anagrams(anagrams: List[String])

case class Words(words: List[String])

case class AreAnagrams(areAnagrams: Boolean)

case class DictionaryStats(wordCount: Int, minWordLength: Int, maxWordLength: Int, medianWordLength: Int, averageWordLength: Double)