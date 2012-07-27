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
import java.net.URLEncoder
import play.api.libs.json.JsValue
import play.api.mvc.AnyContent
import play.libs.Json

class JSONTestSuite extends AssertionsForJUnit {

  @Test def authentificateByApp() {
    running(FakeApplication()) {
      try {
        routeAndCall(FakeRequest(POST, "/register?email=junit@specs.de&firstname=junit&lastname=test&cellphone=017620020200&password.main=123456&password.confirm=123456"))
        val Some(result) = routeAndCall(FakeRequest(GET, "/authenticateByApp/" + URLEncoder.encode("junit@specs.de", "UTF-8") + "/" + "7c4a8d09ca3762af61e59520943dc26494f8941b"))
        assertEquals(OK, status(result))
        assertEquals(Some("application/json"), contentType(result))
        val node2 = Json.parse(contentAsString(result));
        assertEquals(node2.get("firstname").asText(), "junit");
      }
      finally {
        val user = User.findByEmail("junit@specs.de").get.delete
      }
    }
  }

  @Test def getTourStatusValuesByApp() {
    running(FakeApplication()) {
      val Some(result) = routeAndCall(FakeRequest(GET, "/getTourStatusValuesByApp"))
      assertEquals(OK, status(result))
      assertEquals(Some("application/json"), contentType(result))
      val node = Json.parse(contentAsString(result));
      val subNode = node.get(1)
      assertEquals(subNode.get("_3").asText(), "if a taxi was called");
    }
  }

  @Test def getActiveToursByApp() {
    running(FakeApplication()) {
      try {
        routeAndCall(FakeRequest(POST, "/register?email=junit@specs.de&firstname=junit&lastname=test&cellphone=017620020200&password.main=123456&password.confirm=123456"))
        val Some(result) = routeAndCall(FakeRequest(GET, "/getTourStatusValuesByApp"))
        assertEquals(OK, status(result))
        assertEquals(Some("application/json"), contentType(result))
        val node = Json.parse(contentAsString(result));
        val subNode = node.get(1)
        assertEquals(subNode.get("_3").asText(), "if a taxi was called");
      }
      finally {
        val user = User.findByEmail("junit@specs.de").get.delete
      }
    }
  }
  @Test def getTourTemplatesByApp() {
    running(FakeApplication()) {
      try {
        routeAndCall(FakeRequest(POST, "/register?email=junit@specs.de&firstname=junit&lastname=test&cellphone=017620020200&password.main=123456&password.confirm=123456"))
        val Some(result) = routeAndCall(FakeRequest(GET, "/getTourTemplatesByApp/" + URLEncoder.encode("junit@specs.de", "UTF-8")))
        assertEquals(OK, status(result))
        assertEquals(Some("application/json"), contentType(result))
        val node = Json.parse(contentAsString(result));
        val subNode = node.get(1)
        print(subNode)
        assertEquals(subNode.get("_1").asText(), "Wolfsburg");
                assertEquals(subNode.get("_2").asText(), "Carmeq Wolfsburg, Autovision");
      }
      finally {
        val user = User.findByEmail("junit@specs.de").get.delete
      }
    }
  }

}