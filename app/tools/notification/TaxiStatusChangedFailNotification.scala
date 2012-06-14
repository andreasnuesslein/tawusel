package tools.notification

import models._

class TaxiStatusChangedFailNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {
  
  val subject = "Status of planned tour has changed"

  val text = "For unforeseen reasons the taxi could not be ordered.\n"

  val shortText = "Hello " + notifiedUser.firstname + ", the status of a planned tour has changed. " +
    "For unforeseen reasons the taxi could not be ordered."

}