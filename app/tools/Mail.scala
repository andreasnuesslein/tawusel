package tools

import com.typesafe.plugin._
import models.User
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
      val contentSuccess = "You successfully registered:\n\n\tFirst name: " + user.firstname
      val contentLastNameAndEmail = "\n\tLast name: " + user.lastname + "\n\tEmail: " + user.email
      val contentMobilephone = "\n\tMobile phone: " + user.cellphone
      val contentComplete = contentWelcome + contentSuccess + contentLastNameAndEmail + contentMobilephone
      sendMail(subject, user.email, contentComplete)

    }

    def sendNewTaxiCompanion(toUser: User, tour: Tour, newCompanion: User){

      val subject = "A new passenger entered your tour"
      val fromLoc = "For your tour from " + tour.dep_location + " to "
      val toLoc = tour.arr_location + " a new passenger has entered.\n"
      val newPas = "The new passenger is " + newCompanion.firstname + " " + newCompanion.lastname + "."
      val content = fromLoc + toLoc + newPas
      sendMail(subject, user.email, content)

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

    def sendManualCallMail(user: User, taxiNumber: String, id: Long){

      val token = Tour.findById(id).createToken
      val subject = "Taxi ordering"
      val contentIntro = "For this tour a taxi could not be ordered automatically.\n"
      val contentCallACab = "As you are the initiator of this tour, please call a taxi,"
      val contentTaxiNumber = "using the following number:\n\t" + taxiNumber
      val contentConfirmOrder = "If you have ordered successfully, please confirm:\t"
      val contentConfirmLink = "dev.noova.de/tour/" + id + "/confirm/" + token + "\n\n"
      val contentCancelOrder = "If you are not able to call a taxi, please cancel:\t"
      val contentCancelLink = "dev.noova.de/tour/" + id + "/cancel/" + token
      val contentPartOne = contentIntro + contentCallACab + contentTaxiNumber + contentConfirmOrder
      val contentPartTwo = contentConfirmLink + contentCancelOrder + contentCancelLink
      val contentComplete = contentPartOne + contentPartTwo
      sendMail(subject, user.email, contentComplete)

    }

}
