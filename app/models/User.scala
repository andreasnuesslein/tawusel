package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException

case class User(
  email: String,
  forename: String,
  lastname: String,
  cellphone: String,
  password: String
  )

object User {

  // -- Parsers

  /**
   * Parse a User from a ResultSet
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.forename") ~
    get[String]("user.lastname") ~
    get[String]("user.cellphone") ~
    get[String]("user.password") map {
      case e~fn~ln~c~p => User(e, fn, ln, c, p)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where email = {email}").on(
        'email -> email
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
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
         select * from user where
         email = {email} and password = SHA1({password})
        """
      ).on(
        'email -> email,
        'password -> password
      ).as(User.simple.singleOpt)
    }
  }
   
  /**
   * Create a User.
   */
  @throws(classOf[MySQLIntegrityConstraintViolationException])
  def create(user: User): User = {
	  DB.withConnection { implicit connection =>
	  SQL("""
		INSERT 
	    INTO user (email, forename, lastname, cellphone, password) 
	    VALUES ({e},{fn},{ln},{c},SHA1({p}))
	    """).on(
	    'e -> user.email,
	    'fn -> user.forename,
	    'ln -> user.lastname,
	    'c -> user.cellphone,
	        'p -> user.password
	      ).executeUpdate()
		}
	  
	  	user
    
  }
  
}
