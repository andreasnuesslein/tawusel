package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class PassengerLeftNotificationTest extends Specification {
  "PassengerLeftNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User(-1,"max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password",null)
      val testInteractingUser = new User(-1,"tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password",null)

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new PassengerLeftNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testSubject = "A passenger left your tour"
	      testNotification.subject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new PassengerLeftNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testText = "The passenger " + testInteractingUser.firstname + " " + testInteractingUser.lastname + 
	        " has left your tour from " + Location.getName(testTour.dep_location) + " to " +
            Location.getName(testTour.arr_location) + " .\n"
	      testNotification.text must equalTo(testText)
	    }
	  }
	    
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new PassengerLeftNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testShortText = "Hello " + testNotifiedUser.firstname + ", the user " + testInteractingUser.firstname +
	        " " + testInteractingUser.lastname + " has left your tour from " + Tour.getDepatureLocation(testTour.id) +
	        " at " + testTour.departure + "."
	      testNotification.shortText must equalTo(testShortText)
	    }
	  }
	}
  }
}