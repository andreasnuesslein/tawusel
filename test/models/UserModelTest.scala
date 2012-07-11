package models

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication
import net.sf.ehcache.search.expression.EqualTo


class UserModelTest extends Specification {
  //create the dummy object
  val testUser = User(-1,"max.mustermann@carmeq.com", "Max", "Mustermann", "1234567890", "password",null);
  
  "User model" should {
    running(TestServer(9000)) {
     
        "create a user" in {
          running(FakeApplication()) {
            
        	//check if the dummy was created correctly
        	testUser.email must equalTo("max.mustermann@carmeq.com")
        	testUser.firstname must equalTo("Max")
        	testUser.lastname must equalTo("Mustermann")
        	testUser.cellphone must equalTo("1234567890")
        	testUser.password must equalTo("password")
        	
        	//try to create a new user
        	testUser.create must equalTo(testUser)
          }
        }
    
        "retrieve a user by email" in {
          running(FakeApplication()) {
        	User.findByEmail(testUser.email) must not equalTo(null)
          }
        }
    
        "delete a user" in {
          running(FakeApplication()) {
        	testUser.delete must equalTo(true)
          }
        }
    }
  }
  
}