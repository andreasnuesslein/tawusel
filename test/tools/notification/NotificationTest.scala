package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class NotificationTest extends Specification {
  "JoinExistingTourNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User("max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password")
      val testInteractingUser = new User("tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password")
      
       "have the right notifiedUser" in {
	     running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.getNotifiedUser must equalTo(testNotifiedUser)
	    }
	  }
	    
	  "have the right interactingUser" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.getInteractingUser must equalTo(testInteractingUser)
	    }
	  }
	    
	  "have the right tour" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.getTour must equalTo(testTour)
	    }
	  }
    }
  }
}