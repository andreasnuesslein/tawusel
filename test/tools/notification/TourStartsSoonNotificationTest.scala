package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class TourStartsSoonNotificationTest extends Specification {
  "TourStartsSoonNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User(-1,"max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password",null)
      val testInteractingUser = new User(-1,"tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password",null)

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testSubject = "Your tour will start soon"
	      testNotification.subject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testText = "The tour from " + Location.getName(testTour.dep_location) + " to " + 
	        Location.getName(testTour.arr_location) + " will start at " + testTour.departure + "."
	      testNotification.text must equalTo(testText)
	    }
	  }
	    
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testShortText = "Hello " + testNotifiedUser.firstname + ", your taxi on order waits for you at " +
	      	testTour.departure + "."
	      testNotification.shortText must equalTo(testShortText)
	    }
	  }
	    
	  "have the right notifiedUser" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.notifiedUser must equalTo(testNotifiedUser)
	    }
	  }
	}
  }
}