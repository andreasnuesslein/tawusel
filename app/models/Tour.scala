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
import tools.Timer
//comment
case class Tour(id: Long, departure: Date, arrival: Date, dep_location: Long, arr_location: Long, tour_state: Long, mod_id: Long) {

  def createToken(): String = {
    val hash = MessageDigest.getInstance("SHA-1").digest((this.id+this.departure.toString+
      this.arrival+this.mod_id).getBytes).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
    return hash
  }
  def checkToken(token: String): Boolean = {
    return (this.createToken() == token)
  }
  def userJoin(userid: Int): Boolean = {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO user_has_tour VALUES({uid},{tid})").on(
        'uid -> userid,
        'tid -> this.id).execute()
    }
  }
  def userLeave(userid: Int): Boolean = {
    DB.withConnection { implicit connection =>
      SQL("DELETE FROM user_has_tour WHERE user_id={uid} and tour_id={tid}").on(
        'uid -> userid,
        'tid -> this.id).execute()
      var useridsql = SQL("SELECT user_id FROM user_has_tour WHERE tour_id={tid}").on('tid -> this.id)
      var useridlist = useridsql().map( row =>
        row[Int]("user_id") ).toList

      val was_i_mod_before:Boolean = SQL("SELECT mod_id FROM tour where id={tid}").on('tid -> this.id).as(scalar[Int].single) == userid

      if (useridlist.isEmpty) {
        SQL("DELETE FROM tour where id={tid}").on('tid -> this.id).execute()
      } else {
        if(was_i_mod_before) {
          SQL("UPDATE tour SET mod_id={uid} WHERE id={tid}").on('uid -> useridlist(0),'tid -> this.id).executeUpdate()
        }
      }
      // TODO: updated the mod - do all the emailing, notifying and stuff.
      return true
    }
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
      get[Date]("departure") ~
      get[Date]("arrival") ~
      get[Long]("dep_location") ~
      get[Long]("arr_location") ~
      get[Long]("tour_state") ~
      get[Long]("mod_id") map {
        case i ~ dep ~ arr ~ dep_l ~ arr_l ~ t ~ mod => Tour(i, dep, arr, dep_l, arr_l, t, mod)
      }
  }

  def findById(id: Long): Tour = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour where id = {id}").on(
        'id -> id).as(Tour.simple *).head
    }
  }
  

  def findAll(userid:Int = -1): List[(Int, String, String, String, java.util.Date, java.util.Date)] = {
    DB.withConnection { implicit connection =>
      SQL("""select tour.id,town.name,tour.departure,tour.arrival,l1.name as dep_location,l2.name
        from tour
        join location  as l1 on dep_location=l1.id
        join location2 as l2 on arr_location=l2.id
        join town on town.id = l1.town_id
        where tour.id not in (select tour_id from user_has_tour where user_id={id} )
        """).on('id -> userid)
      .as(int("id") ~ str("town.name") ~ str("location.name") ~ str("location2.name") ~ date("departure") ~ date("arrival") map(flatten) * )
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
  
    def findByDepLocation_id(location_id:Long): List[Tour] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour " +
        "where tour.dep_location  ={l}").on(
          'l -> location_id).as(Tour.simple *)
    }
  }

    def findByArrLocation_id(locationDep_id:Long, locationArr_id:Long): List[Tour] = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour " +
      		"where tour.dep_location  ={l} AND tour.arr_location = {la}").on(
      		    'l -> locationDep_id,
      		    'la -> locationArr_id
      		    ).as(Tour.simple *)
    }
  }
    
  def findTemplatesForUser(user_id: Int): List[Tour] = {
    DB.withConnection { implicit connection =>
      // TODO select * where date<now() count 8, countby
      SQL("SELECT * FROM tour JOIN user_has_tour ON tour.id = user_has_tour.tour_id WHERE user_has_tour.user_id = "+user_id).as(Tour.simple *)
    }

  }
  def findAllForUser(user_id: Int): List[Tour] = {
    DB.withConnection { implicit connection =>
      // TODO select * where date>now()
      SQL("SELECT * FROM tour JOIN user_has_tour ON tour.id = user_has_tour.tour_id WHERE user_has_tour.user_id = "+user_id).as(Tour.simple *)
    }

  }

  def create(dep: Date, arr: Date, dep_l : Long, arr_l :Long,
      state: Long, mod: Long): Tour = {
    DB.withConnection { implicit connection =>
      SQL("""INSERT INTO tour(departure,arrival,dep_location,arr_location,
        tour_state,mod_id) VALUES (
        {dep},{arr},{dep_l},{arr_l},{t},{mod})""").on(
        'dep -> dep, 'arr -> arr, 'dep_l -> dep_l,
        'arr_l -> arr_l,
        't -> state, 'mod -> mod ).executeUpdate()
      var tour = SQL("""SELECT * FROM tour WHERE id = last_insert_id();"""
        ).as(Tour.simple *).head
      Timer.notify(tour)
      return tour
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
          ORDER BY departure ASC
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
