package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import java.util.Date

case class Tour(
  id: Long,
  date: Date,
  departure: Date,
  arrival: Date,
  dep_location: Long,
  arr_location: Long,
  comment: String,
  meetingpoint: String,
  authentification: String,
  tour_state: Long,
  mod_id: Long)

object Tour {

  // -- Parsers

  /**
   * Parse a Tour from a ResultSet
   */
  val simple = {
    get[Long]("id") ~
    get[Date]("date") ~
    get[Date]("departure") ~
    get[Date]("arrival") ~
    get[Long]("dep_location") ~
    get[Long]("arr_location") ~
    get[String]("comment") ~
    get[String]("meetingpoint") ~
    get[String]("authentification") ~
    get[Long]("tour_state") ~
    get[Long]("mod_id") map {
      case i ~ da ~ dep ~ arr ~ dep_l ~ arr_l ~ c ~ m ~ a ~ t ~ mod => Tour(i, da, dep, arr, dep_l, arr_l, c, m, a, t, mod)
    }
  }

  // -- Queries

  /**
   * Retrieve a Tour by id.
   */
  def findById(id: Long): Tour = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour where id = {id}").on(
        'id -> id).as(Tour.simple *).head
    }
  }

  /**
   * Retrieve all Tours.
   */
  def findAll(): List[Tour] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour").as(Tour.simple *)
    }
  }

  /**
   * Create a Tour.
   */
  def create(date: Date, dep: Date, arr: Date, dep_l : Long, arr_l :Long, comment: String,
      meet: String, auth: String, tour_id: Long, mod: Long): Tour = {
   var tid :Long =  0
   DB.withConnection { implicit connection =>
      SQL("""
		INSERT INTO  `mydb`.`tour` (
    		  `date` ,
    		  `departure` ,
    		  `arrival` ,
    		  `dep_location` ,
    		  `arr_location` ,
    		  `comment` ,
    		  `meetingpoint` ,
    		  `authentification` ,
    		  `tour_state` ,
    		  `mod_id`
    		  ) VALUES ( {da} , {dep} , {arr} , {dep_l} , {arr_l} , {c} , {m} , {a} , {t} , {mod})
    		  """).on(
        'da -> date,
        'dep -> dep,
        'arr -> arr,
        'dep_l -> dep_l,
        'arr_l -> arr_l,
        'c -> comment,
        'm -> meet,
        'a -> auth,
        't -> tour_id,
        'mod -> mod
    		  ).executeUpdate()
	      SQL("""
		SELECT * FROM tour WHERE id = last_insert_id();"""
	          ).as(Tour.simple *).head
  	}
  }

  /**
   * Delete a Tour.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("""
        		DELETE 
        		FROM tour 
        		WHERE id = {i}
        	""").on(
        'i -> id).executeUpdate()
    }
  }
  
  /**
   * 
   */
  def getDepatureLocation(tourId: Long): String = {
    DB.withConnection { implicit connection =>
      val firstRow = SQL("""
        SELECT * 
        FROM tour 
        WHERE id = {tourId}
      """).on(
          'tourId -> tourId
      ).apply().head
      Location.getName(firstRow[Long]("dep_location"))
	}
  }
  
  /**
   * 
   */
  def getArrivalLocation(tourId: Long): String = {
    DB.withConnection { implicit connection =>
      val firstRow = SQL("""
        SELECT * 
        FROM tour 
        WHERE id = {tourId}
      """).on(
          'tourId -> tourId
      ).apply().head
      Location.getName(firstRow[Long]("arr_location"))
	}
  }
  
}