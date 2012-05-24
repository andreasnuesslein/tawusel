package controllers

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test._
import models.User


class RegistrationTestCorrectData extends Specification{

 "Application" should {
    "work from within a browser" in {
      running(TestServer(9000), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:9000/register")
        
        browser.pageSource must contain("Sign Up")
        browser.pageSource must contain("Account information")
        
        //try to register with correct entries
        browser.$("#firstname_field").text("Max")
        browser.$("#lastname_field").text("Mustermann")
        browser.$("#email_field").text("max.mustermann@carmeq.com")
        browser.$("#cellphone_field").text("0132123456")
        browser.$("#password_main_field").text("tester")
        browser.$("#password_confirm_field").text("tester")
        browser.$("#registerbutton").click()
        browser.$("dl.error").size must equalTo(0)
//        browser.pageSource must not contain("Login")
//        browser.pageSource must not contain("Register")
//        browser.pageSource must contain("Index")
//        browser.pageSource must contain("Hello max.mustermann@carmeq.com")
//        browser.pageSource must contain("Log out")
   
        //try to register with the same email adress
//        browser.goTo("http://localhost:9000/register")
//        browser.$("#firstname_field").text("Max")
//        browser.$("#lastname_field").text("Mustermann")
//        browser.$("#email_field").text("max.mustermann@carmeq.com")
//        browser.$("#cellphone_field").text("0132123456")
//        browser.$("#password_main_field").text("tester")
//        browser.$("#password_confirm_field").text("tester")
//        browser.$("#registerbutton").click()
//        browser.pageSource must contain("This email adress is already in use")
        
      	}
      }
 	}
 
    object dbDelete extends After {
    	def after = (User.delete("max.mustermann@carmeq.com"))
    }
}
