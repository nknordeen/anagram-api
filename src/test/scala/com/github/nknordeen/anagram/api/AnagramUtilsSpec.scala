package com.github.nknordeen.anagram.api
import org.scalatest._
import com.github.nknordeen.anagram.api.AnagramUtils._

class AnagramUtilsSpec extends FlatSpec with Matchers {

  behavior of "isValidWord"

  it should "return true if only lower case letters" in {
    isValidWord("asdfghjkl") shouldBe true
    isValidWord("qwertyuiop") shouldBe true
  }

  it should "return true if only upper case letters" in {
    isValidWord("AASDFGLKF") shouldBe true
    isValidWord("HELLOWORLD") shouldBe true
  }

  it should "return true if upper and lower case letters" in {
    isValidWord("ASDsdfWEROosfd") shouldBe true
  }

  it should "return false if any other character besides the alphabet are in the string" in {
    isValidWord("   ") shouldBe false
    isValidWord("asdf234") shouldBe false
    isValidWord("asdjsf!@#@#/.,") shouldBe false
  }

  behavior of "removeSpaces"

  it should "remove spaces around word" in {
    removeSpaces("asdf") shouldBe Seq("asdf")
    removeSpaces("  nick  ") shouldBe Seq("nick")
    removeSpaces("   Nick") shouldBe Seq("Nick")
    removeSpaces("asdf   ") shouldBe Seq("asdf")
  }

  it should "split into multiple words" in {
    removeSpaces("nick was here") shouldBe Seq("nick", "was", "here")
    removeSpaces("  nick was here  ") shouldBe Seq("nick", "was", "here")
  }

  it should "remove just spaces if there are other characters in it besides alphabet" in {
    removeSpaces("   asdf1334234///  ") shouldBe Seq("asdf1334234///")
  }
}
