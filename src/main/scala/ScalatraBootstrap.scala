import java.sql.DriverManager

import com.github.nknordeen.anagram.api._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  private val DRIVER = "com.mysql.jdbc.Driver"
  Class.forName(DRIVER)
  val connection = DriverManager.getConnection("jdbc:mysql://dockerhost:3306/anagram", "anagram", "anagram")

  implicit val anagramDAO = new AnagramDAO(connection)
  override def init(context: ServletContext) {
    context.mount(new AnagramApi, "/*")
  }
}
