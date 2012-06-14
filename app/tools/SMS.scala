package tools

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.libs.ws.WS
import play.api.Play.current

import models.User
import tools.notification.Notification

/**
 * TODO
 */
object SMS {
  val smsUser = "carmob"
  val smsPassword = "carmeq"
  
  /**
   * TODO
   * 
   * @returns a string which contains the status message from sending the sms
   */
  def send(notification: Notification, debug: Boolean = false) : String = {
    val smsText = notification.getShortText.replaceAll(" ","%20")
    val promise = WS.url(createUrl(notification.getNotifiedUser, smsText, debug)).get()  
    getApiMessage(promise.value.get.body.toInt)
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
