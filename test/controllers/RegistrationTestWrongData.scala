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
        
        //try to register without a forename - shouldn't work
        browser.$("#forename_field").text("")
        browser.$("#lastname_field").text("Mustermann")
        browser.$("#email_field").text("max.mustermann@carmeq.com")
        browser.$("#cellphone_field").text("0132123456")
        browser.$("#password_main_field").text("tester")
        browser.$("#password_confirm_field").text("tester")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 2")
        browser.pageSource must contain("Valid email required")
        
        //try to register without a lastname -shouln't work
        browser.$("#forename_field").text("Max")
        browser.$("#lastname_field").text("")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 2")
        
        //try to register with wrong email address - shouldn't work
        browser.$("#lastname_field").text("Mustermann")
        browser.$("#email_field").text(".mustermann@carmeq.com")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Valid email required")
        
        //try to register without cellphone number - shouldn't work
        browser.$("#email_field").text("max.mustermann@carmeq.com")
        browser.$("#cellphone_field").text("")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 10")
        
        //try to register with wrong cellphone number - shouldn't work
//        browser.$("#cellphone_field").text("aaaaaaaaaaaa")
//        browser.$("#registerbutton").click()
//        browser.pageSource must contain("Choose a valid cellphone number")
        
        //        browser.goTo("http://localhost:9000/register")
//        browser.$("#forename_field").text("Max")
//        browser.$("#lastname_field").text("Mustermann")
//        browser.$("#email_field").text("max.mustermann@carmeq.com")
//        browser.$("#cellphone_field").text("aaaaaaaaaa")
//        browser.$("#password_main_field").text("tester")
//        browser.$("#password_confirm_field").text("tester")
//        browser.$("#registerbutton").click()
//        browser.pageSource must contain("Choose a valid cellphone number")
       
        //try to register without password - shouldn't work
        browser.$("#cellphone_field").text("0132123456")
        browser.$("#password_main_field").text("")
        browser.$("#registerbutton").click()
        browser.pageSource must contain("Minimum length is 6")
        
         //try to register with wrong comfirm password - shouldn't work
//        browser.$("#password_main_field").text("tester")
//        browser.$("#password_confirm_field").text("test")
//        browser.$("#registerbutton").click()
//        browser.pageSource must contain("Passwords don't match")
        
      	}
      }
 	}
}