package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Location(id: Long, town_id: Long, name: String, address: String)

object Location {
  val simple = {
    get[Long]("location.id") ~
      get[Long]("location.town_id") ~
      get[String]("location.name") ~
      get[String]("location.address") map {
        case i ~ t ~ n ~ a => Location(i, t, n, a)
      }
  }
  
  def getById(id: Int): Location = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM location WHERE id={id}").on('id -> id).as(Location.simple.singleOpt).get
    }    
  }

  def getAllSortedByName: List[Location] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM location ORDER BY location.name ASC").as(Location.simple *)
    }
  }

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
        ).as(scalar[Int].singleOpt)
      if(!locId.isEmpty) {
        return locId.get
      } else {
        return -1
      }
    }
  }

}
