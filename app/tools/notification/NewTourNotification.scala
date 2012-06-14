package tools.notification

import models._

class NewTourNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {
  
  val subject = "You created a new tour"

  val forTour = "You created a new tour from " + Location.getName(tour.dep_location)
  val toTour = " to " + Location.getName(tour.arr_location) + " at " + tour.departure
  val tourDate = " on " + tour.date + "."
  val text = forTour + toTour + tourDate

  val shortText = "You created a new tour to " + Location.getName(tour.arr_location) + 
      " at " + tour.departure + " on " + tour.date + "."

}