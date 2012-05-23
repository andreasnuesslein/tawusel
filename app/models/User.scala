package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class User(
  username: String,
  first_name: String,
  last_name: String,
  email: String,
  password: String,
  cellphone: Option[String],
  homephone: Option[String],
  street: Option[String],
  zip: Option[String],
  city: Option[String],
  notes: Option[String]
  )

object User {

  // -- Parsers

  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.username") ~
    get[String]("user.first_name") ~
    get[String]("user.last_name") ~
    get[String]("user.email") ~
    get[String]("user.password") ~
    get[Option[String]]("user.cellphone") ~
    get[Option[String]]("user.homephone") ~
    get[Option[String]]("user.street") ~
    get[Option[String]]("user.zip") ~
    get[Option[String]]("user.city") ~
    get[Option[String]]("user.notes") map {
      case u~fn~ln~e~p~c~h~s~z~ci~no => User(u, fn, ln, e, p, c, h, s, z, ci, no)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByUsername(username: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where username = {username}").on(
        'username -> username
      ).as(User.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve all users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user").as(User.simple *)
    }
  }
  
  /**
   * Authenticate a User.
   */
  def authenticate(username: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where
         username = {username} and password = SHA1({password})
        """
      ).on(
        'username -> username,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          INSERT INTO user(username,first_name,last_name,email,password,
            cellphone,homephone,street,zip,city,notes) VALUES (
            {u},{fn},{ln},{e},SHA1({p}),{cp},{hp},{s},{z},{c},{n}
          )
        """
      ).on(
        'u -> user.username,
        'fn -> user.first_name,
        'ln -> user.last_name,
        'e -> user.email,
        'p -> user.password,
        'cp -> user.cellphone,
        'hp -> user.homephone,
        's -> user.street,
        'z -> user.zip,
        'c -> user.city,
        'n -> user.notes
      ).executeUpdate()

      user

    }
  }
  
}
