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


  val newTourForm = Form(
    tuple(
      "date" -> date,
      "dep_location" -> number,
      "arr_location" -> number,
      "departure" -> date,
      "arrival" -> date,
      "comment" -> text,
      "meetingpoint" -> text,
      "authentication" -> text
    )
  )


  def newTour = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(views.html.tour.newTour(Town.findAll(), Location.findByTown_id(1), townForm))
  }

  def newTourCreate = IsAuthenticated { email => implicit request =>
    println(newTourForm.bindFromRequest)
    newTourForm.bindFromRequest.fold(
      formWithErrors => Ok("XX"),
      tt => {
        val userid = User.getIdByEmail(email)
        println(userid)
        var tour = Tour.create(tt._1, tt._4, tt._5, tt._2, tt._3, tt._6, tt._7, tt._8,1,userid)
        // TODO: sql insert into user_has_tour(tour.id,userid)
        println(tour)
        Redirect(routes.Tours.myTours)
      })
  }

  def myTours = IsAuthenticated { email => implicit request =>
    val user_id = User.getIdByEmail(email)
    Ok(views.html.tour.myTours(Tour.findAllForUser(user_id)))
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
