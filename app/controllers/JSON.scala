package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import java.sql.Date
import com.codahale.jerkson.Json
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import play.api.libs.ws.WS

object JSON extends Controller {

  def authenticateByApp(email:String, hashedPassword:String) = Action {
    val user = User.authenticateWithHashedPassword(email, hashedPassword)
    val json = Json.generate(user)
    Ok(json).as("application/json")
  }
  
  def getTourStatusValuesByApp() = Action {
    DB.withConnection { implicit connection =>
      val states = SQL("""SELECT *
        FROM tour_state"""
      ).as(int("id") ~ str("name") ~ str("description") map(flatten) *)
      val json = Json.generate(states)
      Ok(json).as("application/json")
    }
  }
  
  def getActiveToursByApp(email: String) = Action {
    val user = User.findByEmail(email).get
    val activeTours = Tour.getActiveForUser(user.id)
    val json = Json.generate(activeTours)
    Ok(json).as("application/json")
  }
  
  def getTourTemplatesByApp(email:String) = Action {
    val user = User.findByEmail(email).get
    val tourTemplates = Tour.getTemplatesForUser(user.id)
    val json = Json.generate(tourTemplates)
    Ok(json).as("application/json")
  }
  
  def getAvailableToursByApp(email:String) = Action {
    val user = User.findByEmail(email).get
    val availableTours = Tour.getAvailableForUser(user.id)
    val json = Json.generate(availableTours)
    Ok(json).as("application/json")
  }
    
  def joinTourByApp(email:String, tourId:String) = Action {
	val user = User.findByEmail(email).get
	var tour = Tour.getById(tourId.toLong)
	val isUserJoined = tour.userJoin(user.id)
	if(isUserJoined) {
	  val json = Json.generate(Tour.getTourDetailsForJSON(tourId.toInt))
	  Ok(json).as("application/json")
	} else {
	  val json = "[{\"_1\": \"false\"}]"
	  Ok(json).as("application/json")
	} 	
  }

  def leaveTourByApp(email:String, tourId:String) = Action {
	val user = User.findByEmail(email).get
	var tour = Tour.getById(tourId.toLong)
	val hasUserLeft = tour.userLeave(user.id)
	if(hasUserLeft) {
	  val json = Json.generate(Tour.getTourDetailsForJSON(tourId.toInt))
	  Ok(json).as("application/json")
	} else {
	  val json = "[{\"_1\": \"false\"}]"
	  Ok(json).as("application/json")
	}
  }
  
  def getGoogleEstimate(dep: Int, arr: Int) = Action {
    try {
      val d = Location.getById(dep).address.replace(" ","+")
      val a = Location.getById(arr).address.replace(" ","+")
      val url = "http://maps.googleapis.com/maps/api/distancematrix/json?sensor=false&origins="+d+"&destinations="+a
      
      val res = WS.url(url).get().await(100000).get.body
      val sec = res.substring(res.indexOf("value",res.indexOf("duration"))+9).split("\n")(0)
      val min = math.ceil(sec.toInt/60.0).toInt
      
      val json = "[{'dur': '"+min+"'}]"
      Ok(json).as("application/json")
    } catch {
      case e:NoSuchElementException => Ok("""[{"dur": "-1"}]""").as("application/json")
    }
  }
  
}