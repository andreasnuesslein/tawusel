package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._

case class Town(
  id: Pk[Long],
  name: String
  )

object Town {

  // -- Parsers

  /**
   * Parse a Town from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("town.id") ~
    get[String]("town.name") map{
      case i ~ n => Town(i,n)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a Town by id.
   */
  def findById(id: Long): Option[Town] = {
    DB.withConnection { implicit connection =>
      SQL("select * from town where id = {id}").on(
        'id -> id
      ).as(Town.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve all Towns.
   */
  def findAll(): List[Town] = {
    DB.withConnection { implicit connection =>
      SQL("select * from town").as(Town.simple *)
    }
  }
  
   
  /**
   * Create a Town.
   */
  def create(name: String) = {
	  DB.withConnection { implicit connection =>
	  SQL("""
		INSERT 
	    INTO town (name) 
	    VALUES ({n})
	    """).on(
	    'n -> name
	      ).executeUpdate()
		}
    
  }
  
  /**
   * Delete a Town.
   */
  def delete(id : Long) = {
     DB.withConnection { implicit connection =>
        	SQL("""
        		DELETE 
        		FROM town 
        		WHERE id = {i})
        	""").on(
	    		'i -> id
        	).executeUpdate()
        }
  }
  
}