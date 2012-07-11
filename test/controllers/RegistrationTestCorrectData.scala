package controllers

import org.specs2.mutable._
import play.api.test.Helpers._
import play.api.test._
import models.User


class RegistrationTestCorrectData extends Specification{

  "Application" should {
    "work from within a browser" in new afterContext{
      running(TestServer(9000), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:9000/register")

        browser.pageSource must contain("Register")

        //try to register with correct entries
        browser.$("#firstname").text("Max")
        browser.$("#lastname").text("Mustermann")
        browser.$("#email").text("max.mustermann@carmeq.com")
        browser.$("#cellphone").text("01321234561")
        browser.$("#password_main").text("tester")
        browser.$("#password_confirm").text("tester")

        browser.$("#registerbutton").click()
        browser.$("dl.error").size must equalTo(0)
        browser.pageSource must not contain("Register")
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
        browser.pageSource must contain("This email address is already in use")
      }
    }
  }

  trait afterContext extends After {
    def after = {
      running(FakeApplication()) {
        val user = User.findByEmail("max.mustermann@carmeq.com").get.delete
      }
    }
  }

}
