package models

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test.TestServer
import play.api.test.FakeApplication
import net.sf.ehcache.search.expression.EqualTo


class UserModelTest extends Specification {
  //create the dummy object
  val testUser = User("max.mustermann@carmeq.com", "Max", "Mustermann", "1234567890", "password");
  
  "User model" should {
    running(TestServer(9000)) {
     
        "create a user" in {
          running(FakeApplication()) {
            
        	//check if it was created correctly
        	testUser.email must equalTo("max.mustermann@carmeq.com")
        	testUser.firstname must equalTo("Max")
        	testUser.lastname must equalTo("Mustermann")
        	testUser.cellphone must equalTo("1234567890")
        	testUser.password must equalTo("password")
        	
        	//try to create a new user
        	User.create(testUser) must equalTo(testUser)
          }
        }
    
        "retrieve all users" in {
          running(FakeApplication()) {
        	User.findAll.size must >(0)
          }
        }
    
        "retrieve a user by email" in {
          running(FakeApplication()) {
        	User.findByEmail(testUser.email) must not equalTo(null)
          }
        }
    
        "delete a user" in {
          running(FakeApplication()) {
        	User.delete(testUser.email) must equalTo(true)
          }
        }
    }
  }
  
}