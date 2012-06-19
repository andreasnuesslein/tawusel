package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._

case class User(
  email: String,
  firstname: String,
  lastname: String,
  cellphone: String,
  password: String
  )

/**
 * User object, which encapsulates the attributes email as
 * unique identifier, firstname, lastname, cellphone number
 * and a password. This object contains all required methods
 * to create a new user as a database entry, select a existing
 * user from the database, select all users found in the
 * database and delete a existing user from the database.
 */
object User {

  /**
   * Parse a User from a ResultSet which is generated by a
   * database query before.
   */
  val simple = {
    get[String]("user.email") ~
    get[String]("user.firstname") ~
    get[String]("user.lastname") ~
    get[String]("user.cellphone") ~
    get[String]("user.password") map {
      case e~fn~ln~c~p => User(e, fn, ln, c, p)
    }
  }
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("""
        SELECT *
        FROM user
        WHERE email = {email}
      """).on(
          'email -> email
      ).as(User.simple.singleOpt)
    }
  }

  def getIdByEmail(email: String): Int = {
    DB.withConnection { implicit connection =>
      val id = SQL("""
        SELECT id
        FROM user
        WHERE email = {email}
        """).on(
          'email -> email
        ).as(get[Int]("id") *)
      if(id.length > 0) {
        return id(0)
      } else {
        return 0
      }
    }
  }

  /**
   * Retrieve all users. Returns the sequence of 
   * found users.
   */
  def findAll: Seq[User] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM user").as(User.simple *)
    }
  }

  /**
   * Authenticate a User. Returns the found user which can be
   * authenticated.
   */
  def authenticate(email: String, password: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("""
         SELECT * 
         FROM user 
         WHERE email = {email} AND password = SHA1({password})
      """).on(
          'email -> email,
          'password -> password
      ).as(User.simple.singleOpt)
    }
  }
   
  /**
   * Create a User. Returns the user which was created.
   */
  def create(user: User): User = {
    DB.withConnection { implicit connection =>
      SQL("""
        INSERT 
		INTO user (email, firstname, lastname, cellphone, password) 
		VALUES ({e},{fn},{ln},{c},SHA1({p}))
	  """).on(
		  'e -> user.email,
		  'fn -> user.firstname,
		  'ln -> user.lastname,
		  'c -> user.cellphone,
		  'p -> user.password
	  ).executeUpdate()
    } 
	  user
  }
  
  /**
   * Delete a User. Returns the number of rows which could be
   * deleted.
   */
  def delete(email : String): Boolean = {
    var affectedEntries : Int = 0
    DB.withConnection { implicit connection =>
        affectedEntries = SQL("""
        DELETE 
    	FROM user 
    	WHERE email = {e}
      """).on(
          'e -> email
      ).executeUpdate()
    }
    if(affectedEntries>0) true else false
  }
  
}
