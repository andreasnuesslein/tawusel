package controllers


import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.data.Form
import play.api.mvc.Controller
import play.api.mvc._
import models._
import tools.Mail
import tools.notification.Notification
import tools.notification.TaxiStatusChangedSuccessNotification
import tools.notification.TaxiStatusChangedFailNotification
import tools.notification.ManualCallNotification
import com.codahale.jerkson.Json

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
    newTourForm.bindFromRequest.fold(
      formWithErrors => Ok("XX"),
      tt => {
        val userid = User.getIdByEmail(email)
        val dep = new java.util.Date(tt._3.toLong)
        val arr = new java.util.Date(tt._4.toLong)
        var tour = Tour.create(dep,arr, tt._1, tt._2, 1,userid)
        tour.userJoin(userid)
        Redirect(routes.Tours.tours)
      })
  }

  def tours = IsAuthenticated { email => implicit request =>
    val user_id = User.getIdByEmail(email)
    val active_tours = Tour.findAllForUser(user_id)
    val tour_templates = Tour.findTemplatesForUser(user_id)
    val available_tours = Tour.findAll(user_id)
    Ok(views.html.tours(active_tours, tour_templates, available_tours,
      Town.findAll(), Location.findAll()))

  }

  def joinTour(id:Long) = IsAuthenticated { email => implicit request =>
    val userid = User.getIdByEmail(email)
    val tour = Tour.findById(id)
    tour.userJoin(userid)
    Redirect(routes.Tours.tours).flashing("success" -> "Successfully joined the tour!")

  }

  def leaveTour(id:Long) = IsAuthenticated { email => implicit request =>
    val userid = User.getIdByEmail(email)
    val tour = Tour.findById(id)
    tour.userLeave(userid)
    Redirect(routes.Tours.tours).flashing("success" -> "Successfully left the tour!")
  }

  /*This method is called by clicking a link in the confirmation email and it does update the state of the tour and informs every user connected to it.
    (tour_state.id == 1) = pending
    (tour_state.id == 2) = success
    (tour_state.id == 3) = fail
    (tour_state.id == 4) = done
  */
  def confirmTour(id: Long, token: String) = Action {
    var tour = Tour.findById(id)
    if( tour.checkToken(token) ) {
        if(tour.updateTourState(2)) {
          var notification: Notification = null
          tour.getAllUsers().foreach((user: User) => {
              notification = new TaxiStatusChangedSuccessNotification(user, null, tour)
              Mail.send(notification)
            }
          )
        }
      Ok
    } else {
      //TODO give proper response (not Ok, but Fault/Error/whatever)
      Ok
    }

  }

  /*This method does set the state of the actual tour to fail and informs all connected users.
  */
  def cancelTour(id: Long, token: String) = Action {
    var tour = Tour.findById(id)
    if( tour.checkToken(token) ) {
        if(tour.updateTourState(3)) {
          var notification: Notification = null
          tour.getAllUsers().foreach((user: User) => {
              notification = new TaxiStatusChangedFailNotification(user, null, tour)
              Mail.send(notification)
            }
          )
          notification = new ManualCallNotification(tour.getAllUsers().head, null, tour, true)
          Mail.send(notification)
        }
      Ok("Tour couldn't be booked")
    } else {
      //TODO give proper response (not Ok, but Fault/Error/whatever)
      Ok("error")
    }

  }

  /*This method is given to remotely create an tour for a per mail specified user.*/
  def remoteCreateTour(token: String) = Action {
   //TODO convert string to email, verification, startingpoint, targetpoint, startingtime and endtime
   //TODO create tour with data
     var dep = new java.util.Date(1.toLong);
     var arr = new java.util.Date(1.toLong);
     var dep_l = 1;
     var arr_l = 1;
//     var userid = User.getIdByEmail("mail@mail.com")
     var userid = 5
     var tour = Tour.create(dep,arr, dep_l, arr_l, 1,userid)
     val json = "{\"aaData\" : " + Json.generate(tour) + "}"
     //200 if Ok, 500 if internal error, 400 if bad request, 401 if unauthorised
     Ok("true")
  }

}
