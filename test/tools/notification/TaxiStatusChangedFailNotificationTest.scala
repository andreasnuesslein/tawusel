package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class TaxiStatusChangedFailNotificationTest extends Specification {
  "TaxiStatusChangedFailNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User("max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password")
      val testInteractingUser = new User("tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password")

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new TaxiStatusChangedFailNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testSubject = "Status of planned tour has changed"
	      testNotification.getSubject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new TaxiStatusChangedFailNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testText = "For unforeseen reasons the taxi could not be ordered.\n"
	      testNotification.getText must equalTo(testText)
	    }
	  }
	    
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new TaxiStatusChangedFailNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testShortText = "Hello " + testNotifiedUser.firstname + ", the status of a planned tour has changed. " +
            "For unforeseen reasons the taxi could not be ordered."
	      testNotification.getShortText must equalTo(testShortText)
	    }
	  }
	}
  }
}