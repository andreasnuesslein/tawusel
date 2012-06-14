package tools.notification

import models._

class PassengerLeftNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {

  val subject = "A passenger left your tour"

  val leftPas = "The passenger " + interactingUser.firstname + " " + interactingUser.lastname + " has left your tour "
  val fromLoc = "from " + Location.getName(tour.dep_location) + " to "
  val toLoc = Location.getName(tour.arr_location) + " .\n"
  val text = leftPas + fromLoc + toLoc

  val shortText = "Hello " + notifiedUser.firstname + ", the user " + interactingUser.firstname +
    " " + interactingUser.lastname + " has left your tour from " + Tour.getDepatureLocation(tour.id) +
    " at " + tour.departure + "."

}