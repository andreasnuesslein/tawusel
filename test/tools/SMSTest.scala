package tools

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._
import tools.notification._

class SMSTest extends Specification {
  //create the dummy object
  val testNotifiedUser = User(-1,"max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password",null)
  val testWrongNotifiedUser = User(-1,"tester.testikus@carmeq.com", "Tester", "Testikus", "0a1788745240", "password",null)
  val testInteractingUser = new User(-1,"tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password",null)

  "SMS" should {
    
    /**
     * The following test cases has to run on the test server in an fakeApplication to grant
     * access to the database.
     */
    running(TestServer(9000)) {
      "retrieve the right return message from the database" in {
	    running(FakeApplication()) {
	      SMS.getApiMessage(100) must equalTo("SMS wurde erfolgreich verschickt.")
	      SMS.getApiMessage(202) must equalTo("Empfängernummer ungültig.")
	      SMS.getApiMessage(401) must equalTo("Variable text ist zu lang.")
	    }
	  }
    
      "not send the sms if the cellphone number is incorrect" in {
        running(FakeApplication()) {
          val testTour = Tour.getById(1)
          val testNotification = new JoinExistingTourNotification(testWrongNotifiedUser, testInteractingUser, testTour)
          
          SMS.send(testNotification, true) must equalTo("Empfängernummer ungültig.")
        }
      }
    
      "send a sms for all notification types" in {
        running(FakeApplication()) {
          val testTour = Tour.getById(1)
          
          var testNotification : Notification = new JoinExistingTourNotification(testNotifiedUser, null, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new ManualCallNotification(testNotifiedUser, null, testTour, false)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new NewTourNotification(testNotifiedUser, null, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new PassengerJoinedNotification(testNotifiedUser, testInteractingUser, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new PassengerLeftNotification(testNotifiedUser, testInteractingUser, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new RegisterNotification(testNotifiedUser, null, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new TaxiStatusChangedFailNotification(testNotifiedUser, null, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new TaxiStatusChangedSuccessNotification(testNotifiedUser, null, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
          
          testNotification = new TourStartsSoonNotification(testNotifiedUser, null, testTour)
          SMS.send(testNotification, true) must equalTo("SMS wurde erfolgreich verschickt.")
        }
      }
    }
  }
}