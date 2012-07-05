package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import java.net.URLDecoder
import java.security.MessageDigest

import models._

case class User(id: Int, email: String, firstname: String, lastname: String, cellphone: String, password: String, extension: Option[String]) {
  def create: Int = {
    User.create(this.email,
      this.firstname,
      this.lastname,
      this.cellphone,
      this.password)
  }

  def update(email:String, cellphone: String, extension: Option[String]): Boolean = {
    DB.withConnection { implicit connection =>
      (SQL("UPDATE user SET email={e},cellphone={c}, extension={ex} WHERE id = {id}").on(
        'id -> this.id,
        'e -> email,
        'c -> cellphone,
        'ex -> extension).executeUpdate == 1)
    }
  }
  def passwordResetToken: String = {
    val now = new java.util.Date()
    val today = now.getYear().toString+now.getMonth().toString+now.getDate().toString
    MessageDigest.getInstance("SHA-1").digest((this.email+this.lastname+this.password+today).getBytes)
      .map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
  }

  def checkResetToken(token:String): Boolean = {
    token == passwordResetToken
  }

  def changePassword(pw: String): Boolean = {
    DB.withConnection { implicit connection =>
      (SQL("UPDATE user SET password=SHA1({p}) WHERE id = {id}").on(
        'p -> pw,'id -> this.id).executeUpdate == 1)
    }
  }

}

/**
 * User object, which encapsulates the attributes email as
 * unique identifier, firstname, lastname, cellphone number
 * and a password. This object contains all required methods
 * to create a new user as a database entry, select a existing
 * user from the database, select all users found in the
 * database and delete a existing user from the database.
 */
object User {
  val simple = {
    get[Int]("user.id") ~
    get[String]("user.email") ~
    get[String]("user.firstname") ~
    get[String]("user.lastname") ~
    get[String]("user.cellphone") ~
    get[String]("user.password") ~
    get[Option[String]]("user.extension") map {
      case i~e~fn~ln~c~p~ex => User(i, e, fn, ln, c, p, ex)
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

  def findById(id: Int): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("""
        SELECT *
        FROM user
        WHERE id = {id}
      """).on(
          'id -> id
      ).as(User.simple.singleOpt)
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
   * Authenticate a User. This method is called when the Android app trys to
   * authentificate, therefore it's second parameter is a String of a SHA-1
   * hashed password. Retruns the found user which can be authenticated.
   */
  def authenticateWithHashedPassword(email: String, hashedPassword: String): Option[User] = {
    DB.withConnection { implicit connection =>
      SQL("""
         SELECT * 
         FROM user 
         WHERE email = {email} AND password = {password}
      """).on(
          'email -> email,
          'password -> hashedPassword
      ).as(User.simple.singleOpt)
    }
  }
   
  /**
   * Create a User. Returns the ID
   */
  def create(email:String, firstname: String, lastname: String, cellphone: String, password: String): Int = {
    DB.withConnection { implicit connection =>
      val id: Long = SQL("""
        INSERT
        INTO user (email, firstname, lastname, cellphone, password)
        VALUES ({e},{fn},{ln},{c},SHA1({p}))""").on(
		  'e -> email,
		  'fn -> firstname,
		  'ln -> lastname,
		  'c -> cellphone,
		  'p -> password
	  ).executeInsert().get
    UserNotification.create(id.toInt)
    id.toInt
    }
  }

  
}
