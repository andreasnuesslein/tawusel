package tools.notification

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication

import models._

class ManualCallNotificationTest extends Specification {
  "ManualCallNotification" should {
    running(TestServer(9000)) {
      //dummy users
      val testNotifiedUser = new User("max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password")
      val testInteractingUser = new User("tester.testikus@carmeq.com", "Tester", "Testikus", "0123456789", "password")

      "have the right subject" in {
        running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new ManualCallNotification(testNotifiedUser, testInteractingUser, testTour, true)
	      
	      val testSubject = "Taxi ordering"
	      testNotification.getSubject must equalTo(testSubject)
	    }
      }
      
      "have the right text" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new ManualCallNotification(testNotifiedUser, testInteractingUser, testTour, false)
	      
	      val testText = "Hello " + testNotifiedUser.firstname + " " + testNotifiedUser.lastname + ",\n" + "" +
	        "For the tour from " + Location.getName(testTour.dep_location) + " to " + 
            Location.getName(testTour.arr_location) + " a taxi could not be ordered automatically.\n" +
            "As you are the initiator of this tour, please call a taxi, using the following number:\n\t" +
            testNotification.taxiNumber + "\n\nIf you have ordered successfully, please confirm:\t" +
            "wusel.noova.de/tour/" + testTour.id + "/confirm/" + testNotification.token + "\n\n" +
            "If you are not able to call a taxi, please cancel:\t" +
            "wusel.noova.de/tour/" + testTour.id + "/cancel/" + testNotification.token
	      testNotification.getText must equalTo(testText)
	    }
	  }
      
      "have the right text for new initiator" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new ManualCallNotification(testNotifiedUser, testInteractingUser, testTour, true)
	      
	     val testText = "Hello " + testNotifiedUser.firstname + " " + testNotifiedUser.lastname + ",\n" + "" +
	        "For the tour from " + Location.getName(testTour.dep_location) + " to " + 
            Location.getName(testTour.arr_location) + " a taxi could not be ordered automatically.\n" +
            "As you are the new initiator of this tour, please call a taxi, using the following number:\n\t" +
            testNotification.taxiNumber + "\n\nIf you have ordered successfully, please confirm:\t" +
            "wusel.noova.de/tour/" + testTour.id + "/confirm/" + testNotification.token + "\n\n" +
            "If you are not able to call a taxi, please cancel:\t" +
            "wusel.noova.de/tour/" + testTour.id + "/cancel/" + testNotification.token
	      testNotification.getText must equalTo(testText)
	      testNotification.getText must equalTo(testText)
	    }
	  }
      
	  "have the right shortText" in {
	    running(FakeApplication()) {
	      val testTour = Tour.findById(1)
          val testNotification = new ManualCallNotification(testNotifiedUser, testInteractingUser, testTour,true)
	      
	      val testShortText = "Hello " + testNotifiedUser.firstname + " " + testNotifiedUser.lastname + 
      ", please call a taxi using the number: " + testNotification.taxiNumber
	      testNotification.getShortText must equalTo(testShortText)
	    }
	  }
    }
  }
}
