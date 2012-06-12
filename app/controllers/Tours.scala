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
      "dep_location" -> number,
      "arr_location" -> number,
      "departure" -> text,
      "arrival" -> text
    )
  )


  def newTourCreate = IsAuthenticated { email => implicit request =>
    println(request.body)
    println(newTourForm.bindFromRequest)
    newTourForm.bindFromRequest.fold(
      formWithErrors => Ok("XX"),
      tt => {
        val userid = User.getIdByEmail(email)
        val date = new java.util.Date(tt._3.toLong)
        var tour = Tour.create(date,date,date, tt._1, tt._2, "","","",1,userid)
        tour.updateUserHasTour(userid)
        Redirect(routes.Tours.tours)
      })
  }


  def tours = IsAuthenticated { email => implicit request =>
    val user_id = User.getIdByEmail(email)
    val active_tours = Tour.findAllForUser(user_id)
    val tour_templates = Tour.findTemplatesForUser(user_id)
    val available_tours = Tour.findAll()
    Ok(views.html.tours(active_tours, tour_templates, available_tours,
      Town.findAll(), Location.findAll()))

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


}
