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
      val testNotifiedUser = new User(-1,"max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password",null)
      val testInteractingUser = new User(-1,"tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password",null)
      
       "have the right notifiedUser" in {
	     running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.notifiedUser must equalTo(testNotifiedUser)
	    }
	  }
	    
	  "have the right interactingUser" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.interactingUser must equalTo(testInteractingUser)
	    }
	  }
	    
	  "have the right tour" in {
	    running(FakeApplication()) {
	      val testTour = Tour.getById(1)
          val testNotification = new JoinExistingTourNotification(testNotifiedUser, testInteractingUser, testTour)
	      
	      testNotification.tour must equalTo(testTour)
	    }
	  }
    }
  }
}