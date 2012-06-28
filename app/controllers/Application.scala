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
     Redirect(routes.Tours.tours)
   }
}