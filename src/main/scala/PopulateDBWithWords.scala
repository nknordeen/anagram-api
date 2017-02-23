import java.sql.DriverManager

import com.github.nknordeen.anagram.api.AnagramConfig
import com.github.nknordeen.anagram.api.AnagramDAO._

import scala.io.Source

object PopulateDBWithWords extends App {

  private val DB_HOST = AnagramConfig.get(AnagramConfig.DB_HOST_KEY).getOrElse(AnagramConfig.DB_HOST_DEFAULT)
  private val DB_PORT = AnagramConfig.get(AnagramConfig.DB_HOST_PORT_KEY).getOrElse(AnagramConfig.DB_HOST_PORT_DEFAULT)
  private val DB_USER = AnagramConfig.get(AnagramConfig.DB_USER_KEY).getOrElse(AnagramConfig.DB_USER_DEFAULT)
  private val DB_PASSWORD = AnagramConfig.get(AnagramConfig.DB_PASSWORD_KEY).getOrElse(AnagramConfig.DB_PASSWORD_DEFAULT)
  private val DB_DATABASE = AnagramConfig.get(AnagramConfig.DB_DATABASE_KEY).getOrElse(AnagramConfig.DB_DATABASE_DEFAULT)
  val connection = DriverManager.getConnection(s"jdbc:mysql://$DB_HOST:$DB_PORT/$DB_DATABASE", DB_USER, DB_PASSWORD)

  val CREATE_INDEXED_WORDS =
    s"""CREATE TABLE IF NOT EXISTS indexed_words
       | (word VARCHAR(255) PRIMARY KEY, $ALPHABET_DB_COLUMNS );""".stripMargin
  connection.prepareStatement(CREATE_INDEXED_WORDS).execute()
  // we don't care if the word is already there, so failing to insert is fine
  println("=============================")
  println("Starting import of dictionary")
  println("=============================")

  val statement = connection.prepareStatement(INSERT_WORD)
  Source.fromFile("dictionary.txt")
    .getLines()
    .foreach(word => {
      bindInsertStatement(statement)(word.toLowerCase())
      statement.addBatch()
    })
  statement.executeBatch()
}
