package tools.notification

import models._

class ManualCallNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour,
  newInitiator: Boolean) extends Notification(notifiedUser, interactingUser,tour) {
 
  //TODO find real number 22456 for mobile, with 090011 in advance for phone
  val taxiNumber: String = "22456"
  
  val subject = "Taxi ordering"

  //set text variable
  val token = tour.createToken
  val contentIntro = "Hello " + notifiedUser.firstname + " " + notifiedUser.lastname + ",<br />" + "" +
    "For the tour from " + Location.getName(tour.dep_location) + " to " + 
    Location.getName(tour.arr_location) + " a taxi could not be ordered automatically.<br />"
    
  //check if the user is notified because he is a new initiator
  var contentInitiator = ""
  if(newInitiator) {
    contentInitiator += "As you are the new initiator of this tour, please call a taxi,"
  } else {
    contentInitiator += "As you are the initiator of this tour, please call a taxi,"
  }
  contentInitiator += " using the following number:</br>\t<a href=\"tel:" + taxiNumber + "\">" + taxiNumber + "</a> (or from a landline: <a href=\"tel:09001122456\">09001122456</a>)<br /><br />"
      
  //create the confirm/cancel links
  val contentConfirmOrder = "If you have ordered successfully, please\t"
  val contentConfirmLink = "<a href=\"http://wusel.noova.de/tour/" + tour.id + "/confirm/" + token + "\">confirm the tour</a>.<br /><br />"
  val contentCancelOrder = "If you are not able to call a taxi, please\t"
  val contentCancelLink = "<a href=\"http://wusel.noova.de/tour/" + tour.id + "/cancel/" + token + "\">cancel the tour</a>."  
  val contentLinks = contentConfirmOrder + contentConfirmLink + contentCancelOrder + contentCancelLink
      
  val text = contentIntro + contentInitiator + contentLinks

  val shortText = "Hello " + notifiedUser.firstname + " " + notifiedUser.lastname + 
      ", please call a taxi using the number: " + taxiNumber

}
