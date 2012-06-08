package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import java.util.Date
import java.security.MessageDigest
import java.util.Calendar
//comment
case class Tour(
  id: Long,
  date: Date,
  departure: Date,
  arrival: Date,
  dep_location: Long,
  arr_location: Long,
  comment: Option[String],
  meetingpoint: Option[String],
  authentification: Option[String],
  tour_state: Long,
  mod_id: Long) {

  def createToken(): String = {
    val hash = MessageDigest.getInstance("SHA-1").digest((this.id+this.date.toString+this.departure+
      this.arrival+this.mod_id).getBytes).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
    return hash
  }

  def checkToken(token: String): Boolean = {
    return (this.createToken() == token)
  }

  def getAllUsers(): List[User] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM user JOIN user_has_tour on user.id = user_has_tour.user_id WHERE user_has_tour.tour_id = {tourId};").on(
        'tour_id -> this.id).as(User.simple *)
    }
  }

  def updateTourState(state: Long): Boolean = {
    DB.withConnection { implicit connection =>
      if(SQL("UPDATE tour SET tour_state = {state} WHERE id = {id};").on(
        'state -> state, 'id -> this.id).executeUpdate == 1) {
        return true
      } else {
        return false
      }
    }
  }
}


object Tour {

  val simple = {
    get[Long]("id") ~
      get[Date]("date") ~
      get[Date]("departure") ~
      get[Date]("arrival") ~
      get[Long]("dep_location") ~
      get[Long]("arr_location") ~
      get[Option[String]]("comment") ~
      get[Option[String]]("meetingpoint") ~
      get[Option[String]]("authentification") ~
      get[Long]("tour_state") ~
      get[Long]("mod_id") map {
        case i ~ da ~ dep ~ arr ~ dep_l ~ arr_l ~ c ~ m ~ a ~ t ~ mod => Tour(i, da, dep, arr, dep_l, arr_l, c, m, a, t, mod)
      }
  }

  def findById(id: Long): Tour = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour where id = {id}").on(
        'id -> id).as(Tour.simple *).head
    }
  }
  

  def findAll(): List[Tour] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour").as(Tour.simple *)
    }
  }
 

    def findByTown_id(town_id:Long): List[Tour] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour " +
      		"inner join location " +
      		"on tour.dep_location = location.id " +
      		"where location.town_id ={t}").on(
      		    't -> town_id).as(Tour.simple *)
    }
  }
  
    def findByLocation_id(location_id:Long): List[Tour] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour " +
      		"where tour.dep_location  ={l}").on(
      		    'l -> location_id).as(Tour.simple *)
    }
  }
    
  def findAllForUser(user_id: Int): List[Tour] = {
    DB.withConnection { implicit connection =>
      //"select * from tour join user_has_tour on tour.id = user_has_tour.tour_id where user_has_tour.user_id = 1;"
      SQL("SELECT * FROM tour JOIN user_has_tour ON tour.id = user_has_tour.tour_id WHERE user_has_tour.user_id = "+user_id).as(Tour.simple *)
    }

  }

  def create(date: Date, dep: Date, arr: Date, dep_l : Long, arr_l :Long, comment: String,
      meet: String, auth: String, state: Long, mod: Long): Tour = {
    DB.withConnection { implicit connection =>
      SQL("""INSERT INTO tour(date,departure,arrival,dep_location,arr_location,
        comment,meetingpoint,authentification,tour_state,mod_id) VALUES ({da},
        {dep},{arr},{dep_l},{arr_l},{c},{m},{a},{t},{mod})""").on(
        'da -> date, 'dep -> dep, 'arr -> arr, 'dep_l -> dep_l,
        'arr_l -> arr_l, 'c -> comment, 'm -> meet, 'a -> auth,
        't -> state, 'mod -> mod ).executeUpdate()
      SQL("""SELECT * FROM tour WHERE id = last_insert_id();"""
      ).as(Tour.simple *).head
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
  

  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
    SQL("""DELETE FROM tour WHERE id = {i}""").on(
      'i -> id).executeUpdate()
    }
  }
/**
   * get users of a certain tour 
   * */
  def getAllUsersFor(tour_id:Long): List[User] = {
    DB.withConnection { implicit connection =>
      SQL("""
         SELECT * 
         FROM user JOIN user_has_tour ON user.id = user_has_tour.user_id
         WHERE user_has_tour.tour_id = {tour_id}
      """).on(
          'tour_id -> tour_id
      ).as(User.simple *)
    }
  }
/**
   * find the next tour to notify
   * called by timer
   * */
  def timerCheck:Tour = {
    var tmp:Long = 0
    		DB.withConnection { implicit connection =>
		  tmp =  SQL("""
          SELECT COUNT(*) 
          FROM tour 
          WHERE checked_by_timer = 0 
		  """).as(scalar[Long].single)  
		  }
    		
		  if(tmp > 0)	
		  DB.withConnection { implicit connection =>
		    SQL("""
          SELECT * 
          FROM tour 
          WHERE checked_by_timer = 0 
          ORDER BY date ASC, departure ASC
		  LIMIT 1""").as(Tour.simple *).head	  
		  }
		  else
		    null
	}
  /**
   * tour is checked if already notified
   * called by timer
   * */
  def updateCheckedByTimer(id : Long){
    DB.withConnection { implicit connection =>
      SQL("""
        		UPDATE 
        		tour 
    		  	SET checked_by_timer = 1
        		WHERE id = {i}
        	""").on(
        'i -> id).executeUpdate()
    }
  }
}
