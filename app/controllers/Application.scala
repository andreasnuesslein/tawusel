package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import java.sql.Date
import com.codahale.jerkson.Json

object Application extends Controller with Secured {

  
  
  def index = Action { implicit request =>
   // Akka_Test.insertTour(1,new Date(32432))
    Ok(views.html.index())
  }

  def secure = IsAuthenticated({ email =>
    implicit request =>
      val user = User.findByEmail(email).get
      Ok(views.html.auth.summary(user))
  }, Redirect(routes.Auth.login).flashing("error" -> "You have to login first."))
 
  def listLocationsByTown_Id(town_id:String) = Action {
    val locations = Location.findByTown_id(town_id.toLong);
 
    val json = Json.generate(locations)
 
    Ok(json).as("application/json")
  }

  
  
}


