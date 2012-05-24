package controllers

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test._
import models.User


class RegistrationTestWrongData extends Specification{

 "Application" should {
    "work from within a browser" in {
      running(TestServer(9000), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:9000/register")
        
        browser.pageSource must contain("Sign Up")
        browser.pageSource must contain("Account information")

        
        //try to register without a firstname - shouldn't work
        browser.$("#firstname").text("")
        browser.$("#lastname").text("Mustermann")
        browser.$("#email").text("max.mustermann@carmeq.com")
        browser.$("#cellphone").text("0132123456")
        browser.$("#password_main").text("tester")
        browser.$("#password_confirm").text("tester")

        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 2")
        

        //try to register without a lastname -shouln't work
        browser.$("#firstname").text("Max")
        browser.$("#lastname").text("")

        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 2")
        
        //try to register with wrong email address - shouldn't work
        browser.$("#lastname").text("Mustermann")
        browser.$("#email").text(".mustermann@carmeq.com")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Valid email required")
        
        //try to register without cellphone number - shouldn't work
        browser.$("#email").text("max.mustermann@carmeq.com")
        browser.$("#cellphone").text("")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 10")
        
        //try to register with wrong cellphone number - shouldn't work
        browser.$("#cellphone").text("aaaaaaaaaaaa")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Choose a valid cellphone number")
        
        //        browser.goTo("http://localhost:9000/register")
        browser.$("#firstname").text("Max")
        browser.$("#lastname").text("Mustermann")
        browser.$("#email").text("max.mustermann@carmeq.com")
        browser.$("#cellphone").text("aaaaaaaaaa")
        browser.$("#password_main").text("tester")
        browser.$("#password_confirm").text("tester")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Choose a valid cellphone number")

        //try to register without password - shouldn't work
        browser.$("#cellphone").text("0132123456")
        browser.$("#password_main").text("")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 6")
        
         //try to register with wrong comfirm password - shouldn't work
        browser.$("#password_main").text("tester")
        browser.$("#password_confirm").text("test")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Passwords don't match")
        
      	}
      }
 	}
}
