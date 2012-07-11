package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class PassengerJoinedNotificationTest extends Specification {
  "PassengerJoinedNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User(-1,"max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password",null)
      val testInteractingUser = new User(-1,"tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password",null)

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new PassengerJoinedNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testSubject = "A new passenger entered your tour"
	      testNotification.subject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new PassengerJoinedNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testText = "For your tour from " + Location.getName(testTour.dep_location) + " to " +
	        Location.getName(testTour.arr_location) + " a new passenger has entered.\n" +
	        "The new passenger is " + testInteractingUser.firstname + " " + testInteractingUser.lastname + "."
	      testNotification.text must equalTo(testText)
	    }
	  }
	    
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new PassengerJoinedNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testShortText = "Hello " + testNotifiedUser.firstname + ", the user " + testInteractingUser.firstname +
	        " " + testInteractingUser.lastname + " has joined your tour from " + Tour.getDepatureLocation(testTour.id) +
	        " at " + testTour.departure + "."
	      testNotification.shortText must equalTo(testShortText)
	    }
	  }
	    
	  "have the right notifiedUser" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new PassengerJoinedNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.notifiedUser must equalTo(testNotifiedUser)
	    }
	  }
	}
  }
}