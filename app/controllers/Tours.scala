package controllers

import play.data.validation._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data._
import play.api.mvc._
import play.api._
import scala.util.matching.Regex
import models._
import views._

object Tours extends Controller with Secured {


  def newTour = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(views.html.tour.newTour(Town.findAll(), Location.findByTown_id(1), townForm))
  }
  
    val townForm = Form(
    "name" -> nonEmptyText)

  def myTours = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(views.html.tour.myTours(Tour.findAll()))
  }

}
