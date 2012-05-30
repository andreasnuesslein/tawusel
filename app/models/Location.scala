package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._

case class Location(
  id: Pk[Long],
  town_id : Long,
  name: String,
  address: String
  )


object Location {

  
  // -- Parsers

  /**
   * Parse a Location from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("location.id") ~
    get[Long]("location.town_id") ~
    get[String]("location.name") ~
    get[String]("location.address")map{
      case i~t~n~a=> Location(i,t,n,a)
    }
  }
  
  // -- Queries
  
  /**
   * Retrieve a Location by id.
   */
  def findById(id: Long): Option[Location] = {
    DB.withConnection { implicit connection =>
      SQL("select * from location where id = {id}").on(
        'id -> id
      ).as(Location.simple.singleOpt)
    }
  }
  
  /**
   * Retrieve all Locations.
   */
  def findAll(): List[Location] = {
    DB.withConnection { implicit connection =>
      SQL("select * from location").as(Location.simple *)
    }
  }
  
   
  /**
   * Create a Location.
   */
  def create(location: Location):Location = {
	  DB.withConnection { implicit connection =>
	  SQL("""
		INSERT 
	    INTO location (town_id,name,address) 
	    VALUES ({t},{n},{a})
	    """).on(
	    'n -> location.name,
	    't -> location.town_id,
	    'a -> location.address
	      ).executeUpdate()
		}
    location
  }
  
  /**
   * Delete a Location.
   */
  def delete(id : Long) = {
     DB.withConnection { implicit connection =>
        	SQL("""
        		DELETE 
        		FROM location 
        		WHERE id = {i})
        	""").on(
	    		'i -> id
        	).executeUpdate()
        }
  }
  
}