package tools.notification

import models._

class JoinExistingTourNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {
  
  val subject = "You joined an existing tour"
  
  val joinTour = "You joined an existing tour from "
  val tourInfo = Location.getName(tour.dep_location) + " to " + Location.getName(tour.arr_location) + " on "
  val tourTime = tour.date + " at " + tour.departure + "."
  val text = joinTour + tourInfo + tourTime

  val shortText = "You joined an existing tour to " + Location.getName(tour.arr_location) + " on " + 
      tour.date + " at " + tour.departure + "."

}