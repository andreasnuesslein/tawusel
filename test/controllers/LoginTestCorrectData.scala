package controllers

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class LoginTestCorrectData extends Specification{
  
 "Application" should {
    
    "work from within a browser" in {
      running(TestServer(9000), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:9000/login")
        
        browser.pageSource must contain("Register")
        browser.pageSource must contain("Login")
        
        //try to login with correct data
        browser.$("#email").text("test.test@carmeq.com")
        browser.$("#password").text("123456")
        browser.$("#loginbutton").click()
        browser.$("dl.error").size must equalTo(0)
        browser.pageSource must not contain("Login")
        browser.pageSource must not contain("Register")
        browser.pageSource must contain("Hello test.test@carmeq.com")
        browser.pageSource must contain("Log out")
      }
    }
 }
}