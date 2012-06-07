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
import tools.Mail

object Tours extends Controller with Secured {

  val townForm = Form(
    "name" -> nonEmptyText)

  def newTour = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(views.html.tour.newTour(Town.findAll(), Location.findAll(), townForm))
  }


  def myTours = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(views.html.tour.myTours(Tour.findAll()))
  }


  def confirmTour(id: Long, token: String) = IsAuthenticated { email => implicit request =>
    var tour = Tour.findById(id)
    if( tour.checkToken(token) ) {
      //TODO Tour.update(status == ok)
      //TODO SendSuccessMail
      Ok("Tour was success")
    } else {
      //TODO give proper response (not Ok, but Fault/Error/whatever)
      Ok("error")
    }

  }

  def cancelTour(id: Long, token: String) = IsAuthenticated { email => implicit request =>
    var tour = Tour.findById(id)
    if( tour.checkToken(token) ) {
      //TODO Tour.update(status == fail)
      //TODO SendFailureMail
      Ok("Tour couldn't be booked")
    } else {
      //TODO give proper response (not Ok, but Fault/Error/whatever)
      Ok("error")
    }

  }


}
