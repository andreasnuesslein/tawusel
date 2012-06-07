package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import java.util.Date
import java.security.MessageDigest

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

  def create(date: Date, dep: Date, arr: Date, dep_l : Long, arr_l :Long, comment: String,
      meet: String, auth: String, tour_id: Long, mod: Long): Tour = {
    var tid :Long =  0
    DB.withConnection { implicit connection =>
      SQL("""INSERT INTO tour(date,departure,arrival,dep_location,arr_location,
        comment,meetingpoint,authentification,tour_state,mod_id) VALUES ({da},
        {dep},{arr},{dep_l},{arr_l},{c},{m},{a},{t},{mod})""").on(
        'da -> date, 'dep -> dep, 'arr -> arr, 'dep_l -> dep_l,
        'arr_l -> arr_l, 'c -> comment, 'm -> meet, 'a -> auth,
        't -> tour_id, 'mod -> mod ).executeUpdate()
      SQL("""SELECT * FROM tour WHERE id = last_insert_id();"""
      ).as(Tour.simple *).head
    }
  }

  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
    SQL("""DELETE FROM tour WHERE id = {i}""").on(
      'i -> id).executeUpdate()
    }
  }


}
