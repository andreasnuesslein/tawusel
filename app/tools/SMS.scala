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
  val smsUser = "carmob"
  val smsPassword = "carmeq"
  
  /**
   * Calls the method to create a specific notification text for the user
   * afterwards it calls the calls the createUrl method to send a notification
   * sms via the sms77.de http api to the users cellphone. At last the returning
   * message code is mapped to a message which is safed in the database.
   * 
   * @returns a string which contains the status message from sending the sms
   */
  def sendTourNotification(user: User, tour: Tour, debug: Boolean) : String = {
    val smsText = createTourNotificationText(user, tour)
    val promise = WS.url(createUrl(user, smsText, debug)).get()  
    getApiMessage(promise.value.get.body.toInt)
  } 
  
  /**
   * Overloaded method since default parameters doesn't work :( 
   * 
   * @returns a string which contains the status message from sending the sms
   */
  def sendTourNotification(user: User, tour: Tour) : String = {
    sendTourNotification(user, tour, false)
  }
  
  /**
   * Creates the notification text which reminds the user that a taxi
   * tour starts in the next minutes.
   * 
   * @returns a string which contains text for the sms
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
   * 
   * @returns a string which the gap between the current timestamp and the 
   *          depature time in minutes
   */
  def getTimeToDestination(currentTime: Date, depature: Date): String = {
    val period = new Period(currentTime.getTime(), depature.getTime())
    period.getMinutes.toString
  }
  
  /**
   * Overloaded method since default parameters doesn't work :( 
   * 
   * @returns a string which the gap between the current timestamp and the 
   *          depature time in minutes
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
   * 
   * @returns a date in a shared format
   */
  def createDateFormat(date:Date,time:Date):Date = {
    val df = new SimpleDateFormat("yyyy-MM-dd")
    val tf = new SimpleDateFormat("HH:mm:ss")
    val cf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    cf.parse(df.format(date).toString()+" "+tf.format(time).toString())
  }
  
   /**
   * Calls the method to create a specific userLeft text for the user
   * afterwards it calls the calls the createUrl method to send a notification
   * sms via the sms77.de http api to the users cellphone. At last the returning
   * message code is mapped to a message which is safed in the database.
   * 
   * @returns a string which contains the status message from sending the sms
   */
  def sendUserLeft(user:User, leavingUser: User, tour: Tour, newInitiator:Boolean, debug: Boolean): String = {
    val smsText = createUserLeftText(user, leavingUser, tour, newInitiator)
    val promise = WS.url(createUrl(user, smsText, debug)).get()  
    getApiMessage(promise.value.get.body.toInt)
  }
  
  /**
   * Overloaded method since default parameters doesn't work :( 
   * 
   * @returns a string which contains the status message from sending the sms
   */
  def sendUserLeft(user:User, leavingUser: User, tour: Tour, newInitiator: Boolean): String = {
    sendUserLeft(user, leavingUser, tour, newInitiator, false)
  }
  
   /**
   * Creates the userLeft text which tells the user that a another user
   * has left his tour.
   * 
   * @returns a string which contains text for the sms
   */
  def createUserLeftText(user:User, leavingUser: User, tour: Tour, newInitiator:Boolean): String = {
    var smsText: String = "Hello%20"
    smsText += user.firstname
    smsText += ",%20the%20user%20"
    smsText += leavingUser.firstname 
    smsText += "%20"
    smsText += leavingUser.lastname
    smsText += "%20has%20left%20the%20tour%20from%20"
    smsText += Tour.getDepatureLocation(tour.id).replaceAll(" ","%20")
    smsText += "%20at%20"
    smsText += tour.departure
    smsText += "."
    if(newInitiator) {
      smsText += "%20You%20are%20the%20new%20initiater!"
    }
    smsText
  }

  
  /**
   * Builds the url to call by the http api of sms77.de. 
   * 
   * @returns the string which contains the whole url to call by the http api
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
   * 
   * @returns the string which contains the whole url to call by the http api 
   */
  def createUrl(user: User, smsText: String): String = {
    createUrl(user, smsText: String, false)
  }
  
  /**
   * Maps the message code to a status message given by the documentation of the
   * sms77.de http api.
   * @link{www.sms77.de/api.pdf}
   * 
   * @returns the string which contains sms status message
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