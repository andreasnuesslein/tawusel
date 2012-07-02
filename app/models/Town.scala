package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._

case class Town(
  id: Long,
  name: String
  )

object Town {

  /**
   * Parse a Town from a ResultSet
   */
  val simple = {
    get[Long]("town.id") ~
    get[String]("town.name") map{
      case i ~ n => Town(i,n)
    }
  }
  
  /**
   * Retrieve a Town by id.
   */
  def findById(id: Long): Town = {
    DB.withConnection { implicit connection =>
      SQL("select * from town where id = {id}").on(
        'id -> id
      ).as(Town.simple *).head
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
   * Retrieves all Towns sorted by their names.
   */
  def findAllSortedByName(): List[Town] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM town ORDER BY town.name ASC").as(Town.simple *)
    }
  }

  /**
   * Create a Town.
   */
  def create(name: String) :Town = {
    var id :Long = 0
	  DB.withConnection { implicit connection =>
	  SQL("""
		INSERT 
	    INTO town (name) 
	    VALUES ({n})
	    """).on(
	    'n -> name
	      ).executeUpdate();
	      SQL("""
		SELECT * FROM town WHERE id = last_insert_id();"""
	          ).as(Town.simple *).head
    }
    
  }
  
  /**
   * Delete a Town.
   */
  def delete(id : Long) : Boolean = {
    var result: Int = 0
     DB.withConnection { implicit connection =>
        	result = SQL("""
        		DELETE 
        		FROM town 
        		WHERE id = {i}
        	""").on(
	    		'i -> id
        	).executeUpdate()
        }
     if(result>0) true else false
  }
  
}
