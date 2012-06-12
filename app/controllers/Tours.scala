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

  /*This method is called by clicking a link in the confirmation email and it does update the state of the tour and informs every user connected to it.
    (tour_state.id == 1) = pending
    (tour_state.id == 2) = success
    (tour_state.id == 3) = fail
    (tour_state.id == 4) = done
  */
  def confirmTour(id: Long, token: String) = IsAuthenticated { email => implicit request =>
    var tour = Tour.findById(id)
    if( tour.checkToken(token) ) {
        if(tour.updateTourState(2)) {
          tour.getAllUsers().foreach((user: User) => Mail.sendTaxiStatusMail(user, true))
        }
      Ok("Tour was success")
    } else {
      //TODO give proper response (not Ok, but Fault/Error/whatever)
      Ok("error")
    }

  }

  /*This method does set the state of the actual tour to fail and informs all connected users.
  */
  def cancelTour(id: Long, token: String) = IsAuthenticated { email => implicit request =>
    var tour = Tour.findById(id)
    if( tour.checkToken(token) ) {
        if(tour.updateTourState(3)) {
          tour.getAllUsers().foreach((user: User) => Mail.sendTaxiStatusMail(user, false))
          Mail.sendManualCallMail(tour.getAllUsers().head, tour, "22456", tour.id, true)
        }
      Ok("Tour couldn't be booked")
    } else {
      //TODO give proper response (not Ok, but Fault/Error/whatever)
      Ok("error")
    }

  }

  def remoteCreateTour(token: String) = IsAuthenticated { email => implicit request =>
   //TODO convert string to email, verification, startingpoint, targetpoint, startingtime and endtime
   //TODO create tour with data
     Ok("creating was successful")
 }

}
