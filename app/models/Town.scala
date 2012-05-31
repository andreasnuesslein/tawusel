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
	     id = SQL("""
		"SELECT last_insert_id();"""
	          ).executeUpdate()
		}
    var town = new Town(id,name)
    town
    
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
        		WHERE id = {i})
        	""").on(
	    		'i -> id
        	).executeUpdate()
        }
     if(result>0) true else false
  }
  
}