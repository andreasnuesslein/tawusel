package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class RegisterNotificationTest extends Specification {
  "RegisterNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User("max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password")
      val testInteractingUser = new User("tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password")

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new RegisterNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testSubject = "Welcome to TaWusel"
	      testNotification.getSubject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new RegisterNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testText = "Welcome " + testNotifiedUser.firstname + " " + testNotifiedUser.lastname + " to TaWusel.\n" +
	        "You successfully registered:\n\n\tFirst name:\t" + testNotifiedUser.firstname +
	        "\n\tLast name:\t" + testNotifiedUser.lastname + "\n\tEmail:\t\t" + testNotifiedUser.email +
	        "\n\tMobile phone:\t" + testNotifiedUser.cellphone
	      testNotification.getText must equalTo(testText)
	    }
	  }
	    
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new RegisterNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testShortText = "Welcome to TaWusel " + testNotifiedUser.firstname + " your registration was successfully!"
	      testNotification.getShortText must equalTo(testShortText)
	    }
	  }
	}
  }
}