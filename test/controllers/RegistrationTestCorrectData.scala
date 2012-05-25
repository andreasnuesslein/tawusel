package controllers

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test._
import models.User


class RegistrationTestCorrectData extends Specification{

  def after = (User.delete("max.mustermann@carmeq.com"))
  
 "Application" should {
    "work from within a browser" in {
      running(TestServer(9000), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:9000/register")
        
        browser.pageSource must contain("Sign Up")
        browser.pageSource must contain("Account information")

        //try to register with correct entries
        browser.$("#firstname").text("Max")
        browser.$("#lastname").text("Mustermann")
        browser.$("#email").text("max.mustermann@carmeq.com")
        browser.$("#cellphone").text("01321234561")
        browser.$("#password_main").text("tester")
        browser.$("#password_confirm").text("tester")

        browser.$("#registerbutton").click()
        browser.$("dl.error").size must equalTo(0)
        browser.pageSource must not contain("Login")
        browser.pageSource must not contain("Register")
     //   browser.pageSource must contain("Neue Tour")
        browser.pageSource must contain("Hello Max")
        browser.pageSource must contain("Log out")
   
        //try to register with the same email adress
        browser.goTo("http://localhost:9000/register")
        browser.$("#firstname").text("Max")
        browser.$("#lastname").text("Mustermann")
        browser.$("#email").text("max.mustermann@carmeq.com")
        browser.$("#cellphone").text("0132123456")
        browser.$("#password_main").text("tester")
        browser.$("#password_confirm").text("tester")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("This email adress is already in use")

        
      	}
      }
 	}
 
 
    	
   
}
