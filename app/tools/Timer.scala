package tools
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Cancellable
import akka.actor.Scheduler
import akka.util.duration._

import models._

/** Timer object that can be called from throughout the app.
  *
  * When Tours exist in the database, wait until 30 minutes
  * before the next tour starts and wake up to process that tour.
  * If no tours exist, don't do anything.
  *
  * The poll-function will initiate another lookup of the database.
  * This function is called when a new tour has been created.
  *
  * @author Andreas Nüßlein <andreas@nuessle.in>
  * @version 1.0
  *
  */

object Timer {
  private val system = ActorSystem("jobs")
  private var cancellable:Cancellable = null
  val exi = system.actorOf(Props(new Executor))

  def poll(dur:akka.util.FiniteDuration = 0 seconds) = {
    system.scheduler.scheduleOnce(dur ,exi, null)
  }

}

/**
  * The Executor is an instantiation of an Akka-Actor, ergo will it
  * run concurrent. When started, it'll check the database for tours
  * and will process them if necessary.
  *
  */
class Executor extends Actor {
  def receive = {
    case poll => {

      try {
        var t:Tour = Tour.timerTours.get
        val difference = (t.departure.getTime() - new java.util.Date().getTime())/1000/60

        if(difference <= 30) {
          /* Wake up and execute tour */
          t.book
          Timer.poll()
        } else {
          /* Sleep for X minutes, where X = time-to-next-tour - 30 */
          var sleep = difference - 30
          Timer.poll(sleep minutes)
        }
      } catch {
        case e => {
          /* There are no tours in the future.. */
          //Timer.poll(3 seconds)
        }
      }
    }
  }
}
