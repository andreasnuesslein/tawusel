package tools.notification

import models._

class PassengerJoinedNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {
 
  val subject = "A new passenger entered your tour"

  val fromLoc = "For your tour from " + Location.getName(tour.dep_location) + " to "
  val toLoc = Location.getName(tour.arr_location) + " a new passenger has entered.<br />"
  val newPas = "The new passenger is " + interactingUser.firstname + " " + interactingUser.lastname + "."
  val text = fromLoc + toLoc + newPas

  val shortText = "Hello " + notifiedUser.firstname + ", the user " + interactingUser.firstname +
    " " + interactingUser.lastname + " has joined your tour from " + Tour.getDepatureLocation(tour.id) +
    " at " + tour.departure + "."

}
