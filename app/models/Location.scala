package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._

case class Location(
  id: Long,
  town_id: Long,
  name: String,
  address: String)

object Location {

  // -- Parsers

  /**
   * Parse a Location from a ResultSet
   */
  val simple = {
    get[Long]("location.id") ~
      get[Long]("location.town_id") ~
      get[String]("location.name") ~
      get[String]("location.address") map {
        case i ~ t ~ n ~ a => Location(i, t, n, a)
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
   * Retrieve a Location by town_id.
   */
  def findByTown_id(town_id: Long): List[Location] = {
    DB.withConnection { implicit connection =>
      SQL("select * from location where town_id = {id}").on(
        'id -> town_id).as(Location.simple *)
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
   * Retrieves all Locations sorted by their names.
   */
  def findAllSortedByName(): List[Location] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM location ORDER BY location.name ASC").as(Location.simple *)
    }
  }

  /**
   * Create a Location.
   */
  def create(name: String, town_id: Long, address: String): Location = {
    var id: Long = 0
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
      SQL("""
		SELECT * 
	    FROM location 
	    WHERE id = last_insert_id();
	  """).as(Location.simple *).head
    }
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
    if (affectedEntries > 0) true else false
  }
  
  /**
   * 
   */
  def getName(locationId : Long) : String = {
    DB.withConnection { implicit connection =>
      val firstRow = SQL("""
        SELECT * 
        FROM location 
        WHERE id = {locationId}
      """).on(
          'locationId -> locationId
      ).apply().head
      firstRow[String]("name")
	}
  }

  /**
   *
   */
  def getLocationIdByLocAndTownName(locationName: String, townName: String) : Int = {
    DB.withConnection { implicit connection =>
      val locId = SQL("""
        SELECT location.id
        FROM location
        JOIN town ON town.id = location.town_id
        WHERE location.name = {locationName} AND town.name = {townName}
        """).on(
          'locationName -> locationName,
          'townName -> townName
        ).as(scalar[Int].single)
      if(locId != null) {
        return locId
      } else {
        return 0
      }
    }
  }

}
