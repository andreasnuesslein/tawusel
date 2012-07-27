package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Town(id: Long, name: String)

object Town {
  val simple = {
    get[Long]("town.id") ~
    get[String]("town.name") map{
      case i ~ n => Town(i,n)
    }
  }

  def getById(id: Long): Town = {
    DB.withConnection { implicit connection =>
      SQL("select * from town where id = {id}").on(
        'id -> id
      ).as(Town.simple *).head
    }
  }
  
  def getAll: List[Town] = {
    DB.withConnection { implicit connection =>
      SQL("select * from town").as(Town.simple *)
    }
  }
  
  def getAllSortedByName: List[Town] = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM town ORDER BY town.name ASC").as(Town.simple *)
    }
  }
  
}
