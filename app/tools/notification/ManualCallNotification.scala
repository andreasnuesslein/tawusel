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
  val contentIntro = "Hello " + notifiedUser.firstname + " " + notifiedUser.lastname + ",\n" + "" +
    "For the tour from " + Location.getName(tour.dep_location) + " to " + 
    Location.getName(tour.arr_location) + " a taxi could not be ordered automatically.\n"
    
  //check if the user is notified because he is a new initiator
  var contentInitiator = ""
  if(newInitiator) {
    contentInitiator += "As you are the new initiator of this tour, please call a taxi,"
  } else {
    contentInitiator += "As you are the initiator of this tour, please call a taxi,"
  }
  contentInitiator += " using the following number:\n\t" + taxiNumber + "\n\n"
      
  //create the confirm/cancel links
  val contentConfirmOrder = "If you have ordered successfully, please confirm:\t"
  val contentConfirmLink = "wusel.noova.de/tour/" + tour.id + "/confirm/" + token + "\n\n"
  val contentCancelOrder = "If you are not able to call a taxi, please cancel:\t"
  val contentCancelLink = "wusel.noova.de/tour/" + tour.id + "/cancel/" + token  
  val contentLinks = contentConfirmOrder + contentConfirmLink + contentCancelOrder + contentCancelLink
      
  val text = contentIntro + contentInitiator + contentLinks

  val shortText = "Hello " + notifiedUser.firstname + " " + notifiedUser.lastname + 
      ", please call a taxi using the number: " + taxiNumber

}
