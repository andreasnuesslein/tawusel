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
      val testNotifiedUser = new User("max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password")
      val testInteractingUser = new User("tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password")

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testSubject = "Your tour will start soon"
	      testNotification.getSubject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testText = "The tour from " + Location.getName(testTour.dep_location) + " to " + 
	        Location.getName(testTour.arr_location) + " will start at " + testTour.departure + "."
	      testNotification.getText must equalTo(testText)
	    }
	  }
	    
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      val testShortText = "Hello " + testNotifiedUser.firstname + ", your taxi on order waits for you at " +
	      	testTour.departure + "."
	      testNotification.getShortText must equalTo(testShortText)
	    }
	  }
	    
	  "have the right notifiedUser" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new TourStartsSoonNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.getNotifiedUser must equalTo(testNotifiedUser)
	    }
	  }
	}
  }
}