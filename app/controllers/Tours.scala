package controllers


import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.data.Form
import play.api.mvc.Controller
import models._
import tools.Mail
import tools.notification.Notification
import tools.notification.TaxiStatusChangedSuccessNotification
import tools.notification.TaxiStatusChangedFailNotification
import tools.notification.ManualCallNotification

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
          var notification: Notification = null
          tour.getAllUsers().foreach((user: User) => {
              notification = new TaxiStatusChangedSuccessNotification(user, null, tour)
              Mail.send(notification)
            }
          )
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

  def remoteCreateTour(token: String) = IsAuthenticated { email => implicit request =>
   //TODO convert string to email, verification, startingpoint, targetpoint, startingtime and endtime
   //TODO create tour with data
     Ok("creating was successful")
 }

}
