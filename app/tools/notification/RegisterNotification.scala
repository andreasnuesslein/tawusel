package tools.notification

import models._

class RegisterNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {

  val subject = "Welcome to TaWusel"
  
  val contentWelcome = "Welcome " + notifiedUser.firstname + " " + notifiedUser.lastname + " to TaWusel.\n"
  val contentSuccess = "You successfully registered:\n\n\tFirst name:\t" + notifiedUser.firstname
  val contentLastNameAndEmail = "\n\tLast name:\t" + notifiedUser.lastname + "\n\tEmail:\t\t" + notifiedUser.email
  val contentMobilephone = "\n\tMobile phone:\t" + notifiedUser.cellphone
  val text = contentWelcome + contentSuccess + contentLastNameAndEmail + contentMobilephone

  val shortText = "Welcome to TaWusel " + notifiedUser.firstname + " your registration was successfully!"

}