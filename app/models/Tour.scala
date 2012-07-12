package models

import anorm._
import anorm.SqlParser._
import play.api.cache.Cache
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import java.util.Date
import java.security.MessageDigest
import java.util.Calendar
import tools._
import tools.notification._

//comment

case class RichTour(id: Int, town: Town, l1: Location, l2: Location, dep: Date, arr: Date, mod: User, users: List[User], state: Int)


case class Tour(id: Long, departure: Date, arrival: Date, dep_location: Long, arr_location: Long, tour_state: Long, mod_id: Long) {

  def createToken(): String = {
    val hash = MessageDigest.getInstance("SHA-1").digest((this.id+this.departure.toString+
      this.arrival).getBytes).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
    return hash
  }
  def checkToken(token: String): Boolean = {
    return (this.createToken() == token)
  }

  def userJoin(userid: Int): Boolean = {
    DB.withConnection { implicit connection =>
        SQL("INSERT INTO user_has_tour VALUES({uid},{tid},0)").on(
          'uid -> userid,
          'tid -> this.id).execute()
          // notify
          val initiator = User.findById(userid).get
          var users = this.getAllUsers()
          for (u <- users) {
            if(u != initiator) {
              val un = UserNotification.getForUser(u.id)
              val n = new PassengerJoinedNotification(u,initiator,this)
              if(un.sbdy_joined_email) {
                try {
                  Mail.send(n)
                } catch {
                  case _ => println("DEBUG: EmailException")
                }
              }
              if(un.sbdy_joined_sms) SMS.send(n)
            }
          }
      return true
    }
  }
  def userLeave(userid: Int): Boolean = {
    DB.withConnection { implicit connection =>
        SQL("DELETE FROM user_has_tour WHERE user_id={uid} and tour_id={tid}").on(
          'uid -> userid,
          'tid -> this.id).execute()
        var useridsql = SQL("SELECT user_id FROM user_has_tour WHERE tour_id={tid}").on('tid -> this.id)
        var useridlist = useridsql().map( row => row[Int]("user_id") ).toList

        val was_i_mod_before:Boolean = SQL("SELECT mod_id FROM tour where id={tid}").on('tid -> this.id).as(scalar[Int].single) == userid

        if (useridlist.isEmpty) {
          SQL("DELETE FROM tour where id={tid}").on('tid -> this.id).execute()
        } else {
          if(was_i_mod_before) {
            SQL("UPDATE tour SET mod_id={uid} WHERE id={tid}").on('uid -> useridlist(0),'tid -> this.id).executeUpdate()
            val n = new ManualCallNotification(User.findById(useridlist(0)).get,null,this,true)
            Mail.send(n)
          }
        }

        // notify
        val initiator = User.findById(userid).get
        var users = this.getAllUsers()
        for (u <- users) {
          if(u != initiator) {
            val un = UserNotification.getForUser(u.id)
            val n = new PassengerLeftNotification(u,initiator,this)
            if(un.sbdy_left_email) {
              try {
                  Mail.send(n)
                } catch {
                  case _ => println("DEBUG: EmailException")
                }
            }
            if(un.sbdy_left_sms) {
              SMS.send(n)
            }
          }
        }
    return true
    }
  }

  def getAllUsers(): List[User] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM user JOIN user_has_tour on user.id = user_has_tour.user_id WHERE user_has_tour.tour_id = {tid};").on(
        'tid -> this.id).as(User.simple *)
    }
  }
  def getMod: User = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM user JOIN tour on user.id = tour.mod_id WHERE tour.id = {tid};").on(
            'tid -> this.id).as(User.simple.singleOpt).get
        }
  }

  def updateState(state: Long): Boolean = {
    DB.withConnection { implicit connection =>
      (SQL("UPDATE tour SET tour_state = {state} WHERE id = {id}").on(
        'state -> state, 'id -> this.id).executeUpdate == 1)
    }
  }
  def updateTimerState: Boolean = {
    DB.withConnection { implicit connection =>
      (SQL("UPDATE tour SET checked_by_timer = 1 WHERE id = {id}").on(
        'id -> this.id).executeUpdate == 1)
    }
  }

  def book: Boolean = {
    // TODO API-call when available

    // manual way: call a number
    val mod = this.getMod
    val un = UserNotification.getForUser(mod.id)
    val n = new ManualCallNotification(mod, null, this, false)
    if(un.must_call_email) Mail.send(n)
    if(un.must_call_sms) SMS.send(n)


    // notifications
    for(u : User <- this.getAllUsers()){
      val un = UserNotification.getForUser(u.id)
      val n = new TourStartsSoonNotification(u, null, this)
      if(un.xmin_email) Mail.send(n)
      if(un.xmin_sms) SMS.send(n)
    }

    this.updateTimerState
    return true
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
      Timer.poll()
      return tour
    }
  }

  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
    SQL("""DELETE FROM tour WHERE id = {i}""").on(
      'i -> id).executeUpdate()
    }
  }

  def getById(id: Long): Tour = {
    DB.withConnection { implicit connection =>
      SQL("select * from tour where id = {id}").on(
        'id -> id).as(Tour.simple.singleOpt).get
    }
  }

  def getActiveForUser(user_id: Int): List[RichTour] = {
    DB.withConnection { implicit connection =>
      var tours = SQL("""SELECT tour.id, tour.departure, tour.arrival, tour.mod_id, tour.tour_state,
          town.id,town.name,
          l1.id, l1.name, l1.address,
          l2.id, l2.name, l2.address
        FROM tour
        JOIN user_has_tour ON tour.id = user_has_tour.tour_id
        join location  as l1 on dep_location=l1.id
        join location2 as l2 on arr_location=l2.id
        join town on town.id = l1.town_id
        WHERE user_has_tour.user_id = {user_id} AND tour.departure > NOW()"""
      ).on('user_id -> user_id)
      .as(int("tour.id") ~ date("departure") ~ date("arrival") ~ int("mod_id") ~ int("tour_state") ~
          int("town.id") ~ str("town.name") ~
          int("location.id") ~ str("location.name") ~ str("location.address") ~
          int("location2.id") ~ str("location2.name") ~ str("location2.address") map {
            case i~d~a~m~s~ti~tn~l1i~l1n~l1a~l2i~l2n~l2a => {
              val u = SQL("""SELECT * FROM user JOIN user_has_tour ON user_has_tour.user_id=user.id WHERE tour_id={tid}""").on('tid -> i).as(User.simple *)
              RichTour(i,Town(ti,tn),Location(l1i,ti,l1n,l1a),Location(l2i,ti,l2n,l2a),d,a,User.findById(m).get,u,s)
            }
          } *
      )
      tours
    }
  }

  def getAvailableForUser(userid:Int = -1): List[RichTour] = {
    DB.withConnection { implicit connection =>
      var tours = SQL("""SELECT tour.id, tour.departure, tour.arrival, tour.mod_id, tour.tour_state,
          town.id,town.name,
          l1.id, l1.name, l1.address,
          l2.id, l2.name, l2.address
        FROM tour
        JOIN location  as l1 on dep_location=l1.id
        JOIN location2 as l2 on arr_location=l2.id
        JOIN town on town.id = l1.town_id
        WHERE tour.id not in (select tour_id from user_has_tour where user_id={id} ) and tour.departure > NOW()
        """).on('id -> userid)
        .as(int("tour.id") ~ date("departure") ~ date("arrival") ~ int("mod_id") ~ int("tour_state") ~
          int("town.id") ~ str("town.name") ~
          int("location.id") ~ str("location.name") ~ str("location.address") ~
          int("location2.id") ~ str("location2.name") ~ str("location2.address") map {
            case i~d~a~m~s~ti~tn~l1i~l1n~l1a~l2i~l2n~l2a => {
              val u = SQL("""SELECT * FROM user JOIN user_has_tour ON user_has_tour.user_id=user.id WHERE tour_id={tid}""").on('tid -> i).as(User.simple *)
              RichTour(i,Town(ti,tn),Location(l1i,ti,l1n,l1a),Location(l2i,ti,l2n,l2a),d,a,User.findById(m).get,u,s)
            }
          } *
      )
      tours
    }
  }

  def getHistoryForUser(userid: Int): List[RichTour] = {
    DB.withConnection { implicit connection =>
      var tours = SQL("""SELECT tour.id, tour.departure, tour.arrival, tour.mod_id, tour.tour_state,
          town.id, town.name,
          l1.id, l1.name, l1.address,
          l2.id, l2.name, l2.address
        FROM tour
        JOIN user_has_tour ON tour.id = user_has_tour.tour_id
        JOIN location  AS l1 ON dep_location=l1.id
        JOIN location2 AS l2 ON arr_location=l2.id
        JOIN town ON town.id = l1.town_id
        WHERE user_has_tour.user_id = {userid} AND tour.departure < NOW()
      """).on('userid -> userid)
      .as(int("tour.id") ~ date("departure") ~ date("arrival") ~ int("mod_id") ~ int("tour_state") ~
          int("town.id") ~ str("town.name") ~
          int("location.id") ~ str("location.name") ~ str("location.address") ~
          int("location2.id") ~ str("location2.name") ~ str("location2.address") map {
            case i~d~a~m~s~ti~tn~l1i~l1n~l1a~l2i~l2n~l2a => {
              val u = SQL("""SELECT * FROM user JOIN user_has_tour ON user_has_tour.user_id=user.id WHERE tour_id={tid}""").on('tid -> i).as(User.simple *)
              RichTour(i,Town(ti,tn),Location(l1i,ti,l1n,l1a),Location(l2i,ti,l2n,l2a),d,a,User.findById(m).get,u,s)
            }
          } *
      )
      tours
    }
  }

  /*This method returns at most the 8 most planned tours for a specified user.
    The list of favorised tours should be chached for one day(24h/86400s).*/
  def getTemplatesForUser(user_id: Int): List[RichTour] = {
      DB.withConnection { implicit connection =>
        val favorites: List[RichTour] = SQL("""SELECT *
          FROM favorites
          JOIN location AS l1 ON dep_location = l1.id
          JOIN location2 AS l2 ON arr_location = l2.id
          JOIN town ON town.id = l1.town_id
          WHERE user_id = {user_id} AND resetted_favorite = 0
          LIMIT 5
          """ ).on('user_id -> user_id)
          .as(int("tour.id") ~  date("departure") ~ date("arrival") ~
              int("town.id") ~ str("town.name") ~
              int("location.id") ~ str("location.name") ~ str("location.address") ~
              int("location2.id") ~ str("location2.name") ~ str("location2.address") map {
                case i~d~a~ti~tn~l1i~l1n~l1a~l2i~l2n~l2a => RichTour(i,Town(ti,tn),Location(l1i,ti,l1n,l1a),Location(l2i,ti,l2n,l2a),d,a,null,null,1)
              } *
           )
        var templates = favorites
        if(favorites.length < 5) {
          val number = 5 - favorites.length
          val initial_templates = SQL("""SELECT *
            FROM initials LIMIT {number}
            """).on('number -> number).
            as(int("initial_template.id") ~  date("departure") ~ date("arrival") ~
               int("townid") ~ str("town.name") ~
               int("l1id") ~ str("l1name") ~ str("l1address") ~
               int("l2id") ~ str("l2name") ~ str("l2address") map{ 
                 case i~d~a~ti~tn~l1i~l1n~l1a~l2i~l2n~l2a => RichTour(i,Town(ti,tn),Location(l1i,ti,l1n,l1a),Location(l2i,ti,l2n,l2a),d,a,null,null,1)
            } *)
          templates ++= initial_templates
        }
        return templates
      }
  }

  def getFavoritesForUser(user_id: Int): List[(String, String, String, java.util.Date, java.util.Date, Boolean, Int, Int)] = {
      DB.withConnection { implicit connection =>
        val favorites = SQL("""SELECT *
          FROM favorites
          JOIN location AS l1 ON dep_location = l1.id
          JOIN location2 AS l2 ON arr_location = l2.id
          JOIN town ON town.id = l1.town_id
          WHERE user_id = {user_id}
          """ ).on(
                'user_id -> user_id
              ).as(str("town.name") ~ str("location.name") ~ str("location2.name") ~ date("departure") ~ date("arrival") ~ bool("resetted_favorite") ~ int("user_id") ~ int("tour.id") map(flatten) *)
        return favorites
      }
  }  


  def getTourDetailsForJSON(tour_id: Int): List[(Int, String, String, String, java.util.Date, java.util.Date, Int, List[User], Int)] = {
    DB.withConnection { implicit connection =>
      var tours = SQL("""SELECT tour.id,town.name,tour.departure,tour.arrival,l1.name as dep_location,l2.name, tour.mod_id, tour.tour_state
        FROM tour
        JOIN user_has_tour ON tour.id = user_has_tour.tour_id
        JOIN location  as l1 on dep_location=l1.id
        JOIN location2 as l2 on arr_location=l2.id
        JOIN town on town.id = l1.town_id
        WHERE tour.id = {tour_id} AND tour.departure > NOW()"""
      ).on(
        'tour_id -> tour_id
      ).as(int("id") ~ str("town.name") ~ str("location.name") ~ str("location2.name") ~ date("departure") ~ date("arrival") ~ int("mod_id") ~ int("tour_state") map(flatten) *)
      var x = for(t <- tours;
        p = SQL("""select *
        from user join user_has_tour on user_has_tour.user_id=user.id
        where tour_id={tid}""").on('tid -> t._1).as(User.simple *)
      ) yield (t._1,t._2,t._3,t._4,t._5,t._6,t._7,p,t._8)
      return x
    }
  }








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
  

  /* Timer.scala uses this function. */
  def timerTours:Option[Tour] = {
    DB.withConnection { implicit connection =>
        SQL(""" SELECT *
          FROM tour
          WHERE checked_by_timer = 0
          AND departure > NOW()
          ORDER BY departure ASC
          LIMIT 1""").as(Tour.simple.singleOpt)
    }
  }

  /*This method tries to reset all favorites for one specified user.*/
  def resetFavoritesForUser(user_id: Int): Boolean = {
    DB.withConnection { implicit connection =>
      val dbCount = SQL("""SELECT COUNT(*)
          FROM user_has_tour
          JOIN tour ON user_has_tour.tour_id = tour.id
          WHERE user_has_tour.user_id = {user_id} AND tour.departure < NOW()
          """).on(
          'user_id -> user_id).as(scalar[Long].single)
      val execCount = SQL("""UPDATE user_has_tour
          JOIN tour ON user_has_tour.tour_id = tour.id
          SET resetted_favorite = 1
          WHERE user_has_tour.user_id = {user_id} AND tour.departure < NOW()
          """).on(
          'user_id -> user_id).executeUpdate
      if(dbCount == execCount) {
        return true
      } else {
        return false
      }
    }
  }

  /*This method tries to reset a specified favorit for the given user.*/
  def resetFavorite(user_id: Int, tour_id: Int): Boolean = {
    DB.withConnection { implicit connection =>
      val tour = SQL("""SELECT *
        FROM tour
        WHERE tour.id = {tour_id}
        """).on(
        'tour_id -> tour_id
        ).as(Tour.simple.single)
      println(tour)
      val tour_ids: List[Int] = SQL("""SELECT tour.id
        FROM tour
        JOIN user_has_tour ON user_has_tour.tour_id = tour.id
        WHERE tour.dep_location = {dep_location} AND tour.arr_location = {arr_location}
            AND RIGHT(tour.departure, 8) = RIGHT({departure}, 8) AND RIGHT(tour.arrival, 8) = RIGHT({arrival}, 8)
            AND tour.departure < NOW() AND user_has_tour.user_id = {user_id}
        """).on(
        'dep_location -> tour.dep_location,
        'arr_location -> tour.arr_location,
        'departure -> tour.departure,
        'arrival -> tour.arrival,
        'user_id -> user_id
        ).as(scalar[Int] *)
      println(tour_ids)
      var execCount = 0
      for(tourId <- tour_ids) {
          execCount += SQL("""UPDATE user_has_tour
              JOIN tour ON user_has_tour.tour_id = tour.id
              SET resetted_favorite = 1
             WHERE user_has_tour.user_id = {user_id} AND tour.departure < NOW() AND tour.id = {tour_id}
             """).on(
               'user_id -> user_id,
               'tour_id -> tourId
             ).executeUpdate
      }
      if(tour_ids.length == execCount) {
        return true
      } else {
        return false
      }
    }
  }

  /* This method checks if for some given parameters a tour already is existent.*/
  def checkForSimilarTour(dep: Date, dep_loc: Long, arr_loc: Long, time_range: Int): List[Tour] = {
    var calendar = Calendar.getInstance
    calendar.setTime(dep)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + time_range)
    val dep_max: Date = calendar.getTime
    calendar.setTime(dep)
    calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - time_range)
    calendar.set(Calendar.SECOND, 0)
    val dep_min: Date = calendar.getTime
    DB.withConnection { implicit connection =>
      SQL("""SELECT *
        FROM tour
        WHERE departure <= {dep_max} AND departure >= {dep_min}
              AND dep_location = {dep_loc} AND arr_location = {arr_loc}
        """).on(
          'dep_max -> dep_max,
          'dep_min -> dep_min,
          'arr_loc -> arr_loc,
          'dep_loc -> dep_loc
        ).as(Tour.simple *)
    }
  }

}
