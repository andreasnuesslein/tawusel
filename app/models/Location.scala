package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._

case class Location(
  id: Long,
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
    get[Long]("location.id") ~
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
  def findById(id: Long): Location = {
    DB.withConnection { implicit connection =>
      SQL("select * from location where id = {id}").on(
        'id -> id).as(Location.simple *).head
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
  def create(name: String, town_id: Long, address : String) : Location = {
    var id :Long = 0
    DB.withConnection { implicit connection =>
	  SQL("""
        INSERT 
	    INTO location (town_id,name,address) 
	    VALUES ({t},{n},{a})
	  """).on(
	    'n -> name,
	    't -> town_id,
	    'a -> address
	  ).executeUpdate()
	
	  val querylocationId  = SQL("""
        SELECT last_insert_id() as id;"""
	  ).apply().head
	  id = querylocationId[Long]("id")
    }
    
    val location = new Location(id, town_id, name, address)
    location
  }
  
  /**
   * Delete a Location.
   */
  def delete(id : Long) : Boolean = {
    var affectedEntries : Int = 0
    DB.withConnection { implicit connection =>
      affectedEntries = SQL("""
        DELETE 
        FROM location 
        WHERE id = {i}
      """).on(
        'i -> id
      ).executeUpdate()
    }
    if(affectedEntries>0) true else false
  }
  
}