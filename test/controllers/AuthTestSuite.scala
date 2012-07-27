package controllers
import org.scalatest.junit.AssertionsForJUnit
import scala.collection.mutable.ListBuffer
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import play.api.test._
import play.api.test.Helpers._
import models.User
import org.junit.After

class AuthTestSuite extends AssertionsForJUnit {

  @Test def openRegisterForm = {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(GET, "/register"))

      assertEquals(OK, status(result))
      assertEquals(Some("text/html"), contentType(result))
      assertEquals(Some("utf-8"), charset(result))
      assertTrue(contentAsString(result).contains("<form action=\"/register\" method=\"POST\" class=\"form-horizontal\" >"))
    }
  }

  @Test def tryRegisterWrongInput {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(POST, "/register?email=junit@test.de"))
      assertEquals(400, status(result))
      assertEquals(Some("text/html"), contentType(result))
      assertEquals(Some("utf-8"), charset(result))
      assertTrue(contentAsString(result).contains("field is required"))
    }
  }

  @Test def registerWithRightData {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(POST, "/register?email=junit@specs.de&firstname=junit&lastname=test&cellphone=017620020200&password.main=123456&password.confirm=123456"))
      assertEquals(303, status(result))
    }
  }

  @Test def LoginWithRightData {
    running(FakeApplication()) {
      try {
        val Some(result) = routeAndCall(FakeRequest(POST, "/login?email=junit@specs.de&password=123456"))
        assertEquals(303, status(result))
      }
      finally {
        val user = User.findByEmail("junit@specs.de").get.delete
      }
    }
  }
  @Test def LoginWithWrongData {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(POST, "/login?email=kennt@keiner.de&password=123456"))
      assertEquals(400, status(result))
    }
  }
  @Test def LoginAlredyLoggedIn {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(GET, "/login").withSession("email" -> "junit@specs.de"))
      assertEquals(303, status(result))
    }
  }

  @Test def LoginNoData {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(GET, "/login"))
      assertEquals(Some("text/html"), contentType(result))
      assertEquals(Some("utf-8"), charset(result))
      assertTrue(contentAsString(result).contains("<form action=\"/login\" method=\"POST\" class=\"form-horizontal\" >"))
    }
  }

  @Test def registerPerAppWithRightData {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(POST, "/registerByApp").withFormUrlEncodedBody("email" -> "junit@specs.de", "firstname" -> "junit", "lastname" -> "test", "phone" -> "017620020200", "password" -> "123456"))
      assertTrue(contentAsString(result).contains("success"))
      assertEquals(OK, status(result))
      assertEquals(Some("text/plain"), contentType(result))
    }
  }

  @Test def registerPerAppWithExistingData {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(POST, "/registerByApp").withFormUrlEncodedBody("email" -> "junit@specs.de", "firstname" -> "junit", "lastname" -> "test", "phone" -> "017620020200", "password" -> "123456"))
      assertEquals(400, status(result))
      assertEquals(Some("text/plain"), contentType(result))
      assertTrue(contentAsString(result).contains("registration failed, user exists already"))
    }
  }
  @Test def registerPerAppWithNoData {
    running(FakeApplication()) {
      try {
        val Some(result) = routeAndCall(FakeRequest(POST, "/registerByApp"))
        assertEquals(400, status(result))
        assertEquals(Some("text/plain"), contentType(result))
        assertTrue(contentAsString(result).contains("cannot parse post"))
      }
      finally {
        val user = User.findByEmail("junit@specs.de").get.delete
      }
    }
  }

}
