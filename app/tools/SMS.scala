package tools


import play.api.libs.ws.WS
import play.api.libs.ws.Response
import play.api.db.DB
import play.api.libs.concurrent.Promise
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api._
import play.api.mvc._
import models.Tour
import models.User
import models.Location
import java.util.Date
import java.util.Calendar
import org.joda.time.Period
import java.sql.Timestamp
import java.text.SimpleDateFormat

/**
 * 
 */
object SMS {
  val smsUser = "TaWusel"
  val smsPassword = "smsfuertawusel"
  
  /**
   * 
   */
  def sendTourNotification(user: User, tour: Tour, debug: Boolean) : String = {
    val smsText = createTourNotificationText(user, tour)
    val promise = WS.url(createUrl(user, smsText, debug)).get()  
    getApiMessage(promise.value.get.body.toInt)
  } 
  
  /**
   * Overloaded method since default parameters doesn't work :( 
   */
  def sendTourNotification(user: User, tour: Tour) : String = {
    sendTourNotification(user, tour, false)
  }
  
  /**
   * 
   */
  def createTourNotificationText(user: User, tour: Tour) : String = {
    var smsText: String = "Hello%20"
    smsText += user.firstname
    smsText += ",%20your%20taxi%20on%20order%20waits%20for%20you%20at%20"
    smsText += tour.meetingpoint.get
    smsText += "%20in%20"
    smsText += getTimeToDestination(tour.departure)
    smsText += "%20minutes."
    smsText
  }
  
  /**
   * Retrieves the gap between the current timestamp an the depature of 
   * the tour in minutes and returns it as a string.
   */
  def getTimeToDestination(currentTime: Date, depature: Date): String = {
    val period = new Period(currentTime.getTime(), depature.getTime())
    period.getMinutes.toString
  }
  
  /**
   * Overloaded method since default parameters doesn't work :( 
   */
  def getTimeToDestination(depature: Date) : String = {
    val currentTime = createDateFormat(new Date(), new Date())
    val newDepatureFormat = createDateFormat(new Date(), depature)
	getTimeToDestination(currentTime, newDepatureFormat)
  }
  
  /**
   * Creates a shared timestamp format to compare the sql.time
   * format from the database with the java.util.date format created
   * by getting the current timestamp.
   */
  def createDateFormat(date:Date,time:Date):Date = {
    val df = new SimpleDateFormat("yyyy-MM-dd")
    val tf = new SimpleDateFormat("HH:mm:ss")
    val cf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    cf.parse(df.format(date).toString()+" "+tf.format(time).toString())
 }
  
  
  /**
   * 
   */
  def createUrl(user: User, smsText: String, debug: Boolean): String = {
    var url: String = "http://gateway.sms77.de/?u="
    url += smsUser
    url += "&p=" 
    url += smsPassword 
    url += "&to="
    url += user.cellphone
    url += "&text=" 
    url += smsText
    url += "&type=basicplus"
    url += "&from=TaWusel"
    if(debug) { 
      url+= "&debug=1"
    }
    url
  }
  
  /**
   * Overloaded method since default parameters doesn't work :( 
   */
  def createUrl(user: User, smsText: String): String = {
    createUrl(user, smsText: String, false)
  }
  
  /**
   * 
   */
  def getApiMessage(messageCode: Int) : String = {
	DB.withConnection { implicit connection =>
      val firstRow = SQL("""
        SELECT * 
        FROM sms_api_message 
        WHERE id = {messageCode}
      """).on(
          'messageCode -> messageCode
      ).apply().head
      firstRow[String]("message")
	}
  }
}