package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class JoinExistingTourNotificationTest extends Specification {
  "JoinExistingTourNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User("max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password")
      val testInteractingUser = new User("tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password")

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testSubject = "You joined an existing tour"
	      testNotification.getSubject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testText = "You joined an existing tour from " + Location.getName(testTour.dep_location) + 
	        " to " + Location.getName(testTour.arr_location) + " on " + testTour.date + " at " + 
	        testTour.departure + "."
	      testNotification.getText must equalTo(testText)
	    }
	  }
	    
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testShortText = "You joined an existing tour to " + Location.getName(testTour.arr_location) + " on " + 
	        testTour.date + " at " + testTour.departure + "."
	      testNotification.getShortText must equalTo(testShortText)
	    }
	  }
	}
  }
}