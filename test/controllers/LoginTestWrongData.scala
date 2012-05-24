package controllers

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class LoginTestWrongData extends Specification{

 "Application" should {
    
    "work from within a browser" in {
      running(TestServer(9000), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:9000/login")
        
        browser.pageSource must contain("Register")
        browser.pageSource must contain("Login")
        
        //try to login with wrong password - shouldn't work
        browser.$("#email").text("test.test@carmeq.com")
        browser.$("#password").text("12345")
        browser.$("#loginbutton").click()
        browser.pageSource must contain("Invalid email or password")
      }
    }
 }
}