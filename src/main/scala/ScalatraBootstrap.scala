import java.sql.DriverManager

import com.github.nknordeen.anagram.api._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  private val DB_HOST = AnagramConfig.get(AnagramConfig.DB_HOST_KEY).getOrElse(AnagramConfig.DB_HOST_DEFAULT)
  private val DB_PORT = AnagramConfig.get(AnagramConfig.DB_HOST_PORT_KEY).getOrElse(AnagramConfig.DB_HOST_PORT_DEFAULT)
  private val DB_USER = AnagramConfig.get(AnagramConfig.DB_USER_KEY).getOrElse(AnagramConfig.DB_USER_DEFAULT)
  private val DB_PASSWORD = AnagramConfig.get(AnagramConfig.DB_PASSWORD_KEY).getOrElse(AnagramConfig.DB_PASSWORD_DEFAULT)
  private val DB_DATABASE = AnagramConfig.get(AnagramConfig.DB_DATABASE_KEY).getOrElse(AnagramConfig.DB_DATABASE_DEFAULT)

  val connection = DriverManager.getConnection(s"jdbc:mysql://$DB_HOST:$DB_PORT/$DB_DATABASE", DB_USER, DB_PASSWORD)

  implicit val anagramDAO = new AnagramDAO(connection)

  override def init(context: ServletContext) {
    context.mount(new AnagramApi, "/*")
  }
}
