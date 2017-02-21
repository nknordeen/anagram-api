package com.github.nknordeen.anagram.api

object AnagramConfig {

  val DB_HOST_KEY = "mysql.host"
  val DB_HOST_DEFAULT = "dockerhost"

  val DB_HOST_PORT_KEY = "mysql.host.port"
  val DB_HOST_PORT_DEFAULT = "3306"

  val DB_USER_KEY = "mysql.user"
  val DB_USER_DEFAULT = "anagram"

  val DB_PASSWORD_KEY = "mysql.password"
  val DB_PASSWORD_DEFAULT = "anagram"

  val DB_DATABASE_KEY = "mysql.database"
  val DB_DATABASE_DEFAULT = "anagram"

  def get(key: String): Option[String] = {
    sys.props.get(key)
  }
}
