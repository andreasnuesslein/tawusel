package tools

import com.typesafe.plugin._
import models.User
import tools.SMS
import models.Tour
import play.api.Play.current

object Mail {

    def sendMail(subject: String, recipient: String, contentText: String) {

      val mail = use[MailerPlugin].email
      mail.setSubject(subject)
      mail.addRecipient(recipient)
      mail.addFrom("tawusel@dev.noova.de")
      mail.send(contentText)

    }

    def sendTestMail(){

      val subject = "Automatically generated testmessage"
      val recipient = "tombullmann@googlemail.com"
      val content = "This is just a message for testing the mail functionality, please do not respond."
      sendMail(subject, recipient, content)

    }

    def sendRegisterMail(user: User){

      val subject = "Welcome to TaWusel"
      val contentWelcome = "Welcome " + user.firstname + " " + user.lastname + " to TaWusel.\n"
      val contentSuccess = "You successfully registered:\n\n\tFirst name:\t" + user.firstname
      val contentLastNameAndEmail = "\n\tLast name:\t" + user.lastname + "\n\tEmail:\t\t" + user.email
      val contentMobilephone = "\n\tMobile phone:\t" + user.cellphone
      val contentComplete = contentWelcome + contentSuccess + contentLastNameAndEmail + contentMobilephone
      sendMail(subject, user.email, contentComplete)

    }

    def sendNewTourMail(user: User, tour: Tour){

      val subject = "You created a new tour"
      val forTour = "You created a new tour from " + tour.dep_location
      val toTour = " to " + tour.arr_location + " at " + tour.departure.toString
      val tourDate = " on " + tour.date + "."
      val content = forTour + toTour + tourDate
      sendMail(subject, user.email, content)

    }

    def sendJoinExistingTourMail(user: User, tour: Tour){

      val subject = "You joined an existing tour"
      val joinTour = "You joined an existing tour from "
      val tourInfo = tour.dep_location + " to " + tour.arr_location + " on "
      val tourTime = tour.date + " at " + tour.departure + "."
      val content = joinTour + tourInfo + tourTime
      sendMail(subject, user.email, content)

    }

    def sendNewTaxiCompanionMail(toUser: User, tour: Tour, newCompanion: User){

      val subject = "A new passenger entered your tour"
      val fromLoc = "For your tour from " + tour.dep_location + " to "
      val toLoc = tour.arr_location + " a new passenger has entered.\n"
      val newPas = "The new passenger is " + newCompanion.firstname + " " + newCompanion.lastname + "."
      val content = fromLoc + toLoc + newPas
      sendMail(subject, toUser.email, content)

    }

    def sendTaxiStatusMail(user: User, ordered: Boolean){

      val subject = "Status of planned tour has changed"
      val contentSuccess = "The status of the tour has changed to successfully ordered.\n"
      val contentFail = "For unforeseen reasons the taxi could not be ordered.\n"
      if(ordered) {
        val contentComplete = contentSuccess
        sendMail(subject, user.email, contentComplete)
      } else {
        val contentComplete = contentFail
        sendMail(subject, user.email, contentComplete)
      }

    }

    def sendManualCallMail(user: User, tour: Tour, taxiNumber: String, id: Long, newInitiator: Boolean){

      val token = Tour.findById(id).createToken
      val subject = "Taxi ordering"
      val tourSpecs = "from " + tour.dep_location + " to " + tour.arr_location
      val contentIntro = "For the tour " + tourSpecs + " a taxi could not be ordered automatically.\n"
      val contentCallACabNewInitiator= "As you are the new initiator of this tour, please call a taxi,"
      val contentCallACab = "As you are the initiator of this tour, please call a taxi,"
      val contentTaxiNumber = "using the following number:\n\t" + taxiNumber
      val contentConfirmOrder = "If you have ordered successfully, please confirm:\t"
      val contentConfirmLink = "dev.noova.de/tour/" + id + "/confirm/" + token + "\n\n"
      val contentCancelOrder = "If you are not able to call a taxi, please cancel:\t"
      val contentCancelLink = "dev.noova.de/tour/" + id + "/cancel/" + token
      var contentPartOne = ""
      if(newInitiator) {
        contentPartOne += contentIntro + contentCallACab + contentTaxiNumber + contentConfirmOrder
      } else {
        contentPartOne += contentIntro + contentCallACab + contentTaxiNumber + contentConfirmOrder
      }
      val contentPartTwo = contentConfirmLink + contentCancelOrder + contentCancelLink
      val contentComplete = contentPartOne + contentPartTwo
      sendMail(subject, user.email, contentComplete)

    }

    def sendInAdvanceMail(user: User, tour: Tour){

      val minutes = SMS.getTimeToDestination(tour.departure)
      val subject = "Your tour will start soon"
      val fromTo = "The tour from " + tour.dep_location + " to " + tour.arr_location
      val willStart = " will start in " + minutes + "."
      val content = fromTo + willStart
      sendMail(subject, user.email, content)

    }

}
