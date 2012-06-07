package tools

import org.specs2.mutable._
import play.api.test.Helpers._
import models.User
import play.api.test.TestServer
import play.api.test.FakeApplication
import java.util.Date
import java.util.Calendar
import play.api.db.DB
import play.api.libs.ws.WS
import play.api.libs.ws.Response
import play.api.db.DB
import play.api.libs.concurrent.Promise
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import models.Tour
import java.sql.Time
import java.sql.Timestamp

class SMSTest extends Specification {
  //create the dummy object
  val testUser = User("max.mustermann@carmeq.com", "Max", "Mustermann", "01788745240", "password");
  val wrongTestUser = User("tester.testikus@carmeq.com", "Tester", "Testikus", "0a1788745240", "password");

  "SMS" should {
    
    "create the right request url" in {
      SMS.createUrl(testUser, "meine%20erste%20sms", true) must equalTo("http://gateway.sms77.de/?u=" +
        SMS.smsUser + "&p=" + SMS.smsPassword + "&to=" + testUser.cellphone + 
      	"&text=meine%20erste%20sms&type=basicplus&from=TaWusel&debug=1") 
    }
    
    /**
     * The following test cases has to run on the test server in an fakeApplication to grant
     * access to the database.
     */
    running(TestServer(9000)) {
    
      "retrieve the right return message from the database" in {
	    running(FakeApplication()) {
	      SMS.getApiMessage(100) must equalTo("SMS wurde erfolgreich verschickt.")
	      SMS.getApiMessage(202) must equalTo("Empfängernummer ungültig.")
	      SMS.getApiMessage(401) must equalTo("Variable text ist zu lang.")
	    }
	  }
    
      "get the right time to a destination time" in {
        //create a dummy date objects
        val currentDate: Date = new Date(Calendar.getInstance().getTime().getTime())
        var depatureDate: Date = new Date(currentDate.getTime()+240000)
        SMS.getTimeToDestination(currentDate, depatureDate) must equalTo("4")
        SMS.getTimeToDestination(depatureDate) must (equalTo("3") or equalTo("4"))
      }
      
      "not send the sms if the cellphone number is incorrect" in {
        running(FakeApplication()) {
          val testTour = Tour.findById(1)
          SMS.sendTourNotification(wrongTestUser, testTour, true) must equalTo("Empfängernummer ungültig.")
        }
      }
      
      "send a sms tour notification" in {
        running(FakeApplication()) {
          //adapt the db entry for the test
          DB.withConnection { implicit connection =>
            SQL("""
              UPDATE tour 
              SET departure = {dep_time}, arrival = {arr_time}
              WHERE id = 1
            """).on(
              'dep_time -> new Date(Calendar.getInstance().getTime().getTime()+600000),
              'arr_time -> new Date(Calendar.getInstance().getTime().getTime()+300000)
            ).executeUpdate()
            
            val testTour = Tour.findById(1)
            SMS.sendTourNotification(testUser, testTour, true) must equalTo("SMS wurde erfolgreich verschickt.")
          }
        }
      }
    
      "send a sms userLeft" in {
        running(FakeApplication()) {
          val testTour = Tour.findById(1)
          SMS.sendUserLeft(testUser, wrongTestUser, testTour, true, true) must equalTo("SMS wurde erfolgreich verschickt.")
        }
      }
    }
  }
}