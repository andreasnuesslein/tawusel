package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import java.util.Date

case class Tour(
  id: Pk[Long],
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
    get[Pk[Long]]("id") ~
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
  def findById(id: Long): Option[Tour] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour where id = {id}").on(
        'id -> id).as(Tour.simple.singleOpt)
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
  def create(tour: Tour): Tour = {
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
        'da -> tour.date,
        'dep -> tour.departure,
        'arr -> tour.arrival,
        'dep_l -> tour.dep_location,
        'arr_l -> tour.arr_location,
        'c -> tour.comment,
        'm -> tour.meetingpoint,
        'a -> tour.authentification,
        't -> tour.tour_state,
        'mod -> tour.mod_id
    		  ).executeUpdate()
    }
    tour
  }

  /**
   * Delete a Tour.
   */
  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("""
        		DELETE 
        		FROM tour 
        		WHERE id = {i})
        	""").on(
        'i -> id).executeUpdate()
    }
  }

}