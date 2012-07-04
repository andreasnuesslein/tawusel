package tools.notification

import models._
import org.joda.time.Period
import java.text.SimpleDateFormat
import java.util.Date

class TourStartsSoonNotification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {

  val subject = "Your tour will start soon"

  val depTime = tour.departure.toString.take(tour.departure.toString.length - 2)

  val fromTo = "The tour from " + Location.getName(tour.dep_location) + " to " + Location.getName(tour.arr_location)
  val willStart = " will start at " + depTime + "."
  val text = fromTo + willStart

  val shortText = "Hello " + notifiedUser.firstname + ", your taxi on order waits for you at " +
      depTime + "."

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

}
