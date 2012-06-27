package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._

import models._

case class UserNotification(
  userid: Int,
  sbdy_joined_email: Boolean,
  sbdy_joined_sms: Boolean,
  sbdy_left_email: Boolean,
  sbdy_left_sms: Boolean,
  xmin_email: Boolean,
  xmin_sms: Boolean,
  must_call_email: Boolean,
  must_call_sms: Boolean,
  status_changed_email: Boolean,
  status_changed_sms: Boolean
)


object UserNotification {
  val simple = {
    get[Int]("userid") ~
    get[Boolean]("sbdy_joined_email") ~
    get[Boolean]("sbdy_joined_sms") ~
    get[Boolean]("sbdy_left_email") ~
    get[Boolean]("sbdy_left_sms") ~
    get[Boolean]("xmin_email") ~
    get[Boolean]("xmin_sms") ~
    get[Boolean]("must_call_email") ~
    get[Boolean]("must_call_sms") ~
    get[Boolean]("status_changed_email") ~
    get[Boolean]("status_changed_sms") map {
      case id ~ a ~ b ~ c ~ d ~ e ~ f ~ g ~ h ~ i ~ j => UserNotification(id,a,b,c,d,e,f,g,h,i,j)
    }
  }

  def getForUser(id: Int): UserNotification = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM notification where userid = {id}").on(
        'id -> id).as(UserNotification.simple *).head
    }
  }

  def create(id: Int): Boolean = {
    DB.withConnection { implicit connection =>
      // Try to set some sane values here..
      SQL("INSERT INTO notification VALUES ({id},0,0,0,0,1,1,1,1,0,1)")
        .on('id -> id).execute()
    }
  }

  def update(id:Int, a:Boolean, b:Boolean,c:Boolean, d:Boolean, e:Boolean, f:Boolean, g:Boolean,
    h:Boolean, i:Boolean, j:Boolean): Boolean = {
    DB.withConnection { implicit connection =>
    SQL("""DELETE FROM notification WHERE userid={id}""").on('id -> id).execute()
    SQL("""INSERT INTO notification VALUES ({id},{a},{b},{c},{d},{e},{f},{g},{h},{i},{j})""")
      .on('id -> id, 'a -> a, 'b -> b, 'c -> c, 'd -> d,'e -> e,'f -> f,'g -> g,'h -> h,'i -> i,'j -> j).execute()
    }
  }

}

