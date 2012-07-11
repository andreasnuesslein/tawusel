package controllers


import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.text
import play.api.data.Forms.tuple
import play.api.data.Form
import play.api.mvc.Controller
import play.api.mvc._
import models._
import views._
import tools.{Mail,SMS}
import tools.notification.Notification
import tools.notification.TaxiStatusChangedSuccessNotification
import tools.notification.TaxiStatusChangedFailNotification
import tools.notification.ManualCallNotification
import com.codahale.jerkson.Json
import java.util.Calendar
import java.text.SimpleDateFormat

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
      formWithErrors => BadRequest("X"),
      tt => {
        val user = User.findByEmail(email).get
        val dep = new java.util.Date(tt._3.toLong)
        val arr = new java.util.Date(tt._4.toLong)
        var tour = Tour.create(dep,arr, tt._1, tt._2, 1,user.id)
        tour.userJoin(user.id)
        Redirect(routes.Tours.tours).flashing("success" -> "Successfully created the tour!")
      })
  }

  /* This method is being called if the /tours site is being requested and returns the site with its filled lists for full content. */
  def tours = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    val active_tours = Tour.findAllForUser(user.id)
    val tour_templates = Tour.findTemplatesForUser(user.id)
    val available_tours = Tour.findAll(user.id)
    var towns = Town.findAllSortedByName()
    var locations = Location.findAllSortedByName()
    Ok(views.html.tours(active_tours, tour_templates, available_tours, towns, locations))
  }
  
  def joinTour(id:Long) = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    val tour = Tour.findById(id).get
    val users = tour.getAllUsers
    if ( (tour.tour_state==2) && (users.length %4 ==0) ) {
      Redirect(routes.Tours.tours).flashing("error" -> "Sorry, but this tour is already booked and full.")
    } else {
      tour.userJoin(user.id)
      Redirect(routes.Tours.tours).flashing("success" -> "Successfully joined the tour!")
    }
  }

  def leaveTour(id:Long) = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    val tour = Tour.findById(id).get
    tour.userLeave(user.id)
    Redirect(routes.Tours.tours).flashing("success" -> "Successfully left the tour!")
  }

  /*This method is called by clicking a link in the confirmation email and it does update the state of the tour and informs every user connected to it.
    (tour_state.id == 1) = pending
    (tour_state.id == 2) = success
    (tour_state.id == 3) = fail
    (tour_state.id == 4) = done
  */
  def confirmTour(id: Long, token: String) = Action {implicit request =>
    try {
      var tour = Tour.findById(id).get
      if( tour.checkToken(token) ) {
        if(tour.updateTourState(2)) {
          tour.getAllUsers().foreach(u => {
            val un = UserNotification.getForUser(u.id)
            val n = new TaxiStatusChangedSuccessNotification(u, null, tour)
            if(un.status_changed_email) Mail.send(n)
            if(un.status_changed_sms) SMS.send(n)
            }
          )
        }
        Ok(html.status("Taxi-Ordering", "You changed the status of the tour successfully, every passenger will be informed, that a taxi has been ordered."))
      } else {
        Ok(html.status("Error occured", "Due to bad request, nothing changed."))
      }
    } catch {
      case e => Ok(html.status("Missing tour", "This is not the tour you're looking for."))
    }
  }

  /*This method does set the state of the actual tour to fail and informs all connected users.
  */
  def cancelTour(id: Long, token: String) = Action { implicit request =>
    var tour = Tour.findById(id).get
    if( tour.checkToken(token) ) {
        var statusText = ""
        if(tour.updateTourState(3)) {
          tour.getAllUsers().foreach(u => {
              val un = UserNotification.getForUser(u.id)
              val n = new TaxiStatusChangedFailNotification(u, null, tour)
              if(un.status_changed_email) Mail.send(n)
              if(un.status_changed_sms) SMS.send(n)
            }
          )
          if(tour.getAllUsers().length > 1) {
            var n:Notification = null
            if(User.findByEmail(tour.getAllUsers().head.email).get.id.equals(tour.mod_id.toInt)) {
              val userToNotify = tour.getAllUsers().tail.head
              n = new ManualCallNotification(userToNotify, null, tour, true)
            } else {
              n = new ManualCallNotification(tour.getAllUsers().head, null, tour, true)
            }

            Mail.send(n)
            SMS.send(n)
            statusText = "The status of the tour has not changed. The next passenger will be informed to call a taxi."
          } else {
            statusText = "The status of the tour has not changed. We are sorry to inform you, that there is no other passenger to call a taxi."
          }
        }
      Ok(html.status("Taxi-Ordering", statusText))
    } else {
      Ok(html.status("Error occured", "Due to bad request, nothing changed."))
    }
  }

  /*This method is given to remotely create a tour for a per mail specified user.*/
  def remoteCreateTour(uemail: String, hash: String, start: String, end: String, stime: String, etime: String, city: String) = Action {
     val user = User.findByEmail(uemail).get
     val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     val dep = dateFormat.parse(stime)
     val arr = dateFormat.parse(etime)
     var dep_l = 16;
     var arr_l = 17;
     val temp_dl = Location.getLocationIdByLocAndTownName(start, city)
     val temp_al = Location.getLocationIdByLocAndTownName(end, city)
     if(temp_dl > 0 && temp_al > 0) {
       dep_l = temp_dl
       arr_l = temp_al
     }
     val existingTours: List[Tour] = Tour.checkForSimilarTour(dep, dep_l, arr_l, 5)
     if(existingTours.nonEmpty) {
       val existingTour: Tour = existingTours.head
       if(!existingTour.getAllUsers().contains(user)) {
         existingTour.userJoin(user.id)
       }
     } else {
       var tour = Tour.create(dep,arr, dep_l, arr_l, 1,user.id)
     }
     Ok("true")
  }

  /*This method resets all favorites for one specified user. */
  def resetFavoritesForUser(email: String) = Action {
    val user = User.findByEmail(email).get
    if(Tour.resetFavoritesForUser(user.id)) {
      Redirect(routes.Auth.account).flashing("success" -> "Successfully resetted tour favorites!")
    } else {
      Redirect(routes.Auth.account).flashing("warning" -> "Resetting the tour favorites was not successful. Please try again later or contact the support if this problem is ongoing.")
    }
  }

  /*This method resets a favorite which is specified by its tour_id and user_id. */
  def resetFavorite(user_id: Int, tour_id: Int) = Action {
    if(Tour.resetFavorite(user_id, tour_id)) {
      Redirect(routes.Auth.account).flashing("success" -> "Successfully resetted the favorite!")
    } else {
      Redirect(routes.Auth.account).flashing("warning" -> "Resetting the favorites was not successful. Please try again later or contact the support if this problem is ongoing.")
    }
  }

}
