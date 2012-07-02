package tools.notification

import models._

class TaxiStatusChangedSuccessNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {
 
  val subject = "Status of planned tour has changed"

  val text = "The status of the tour has changed to successfully ordered.<br />"

  val shortText = "Hello " + notifiedUser.firstname + ", the status of a planned tour has changed. " +
    "The status of the tour has changed to successfully ordered."

}
