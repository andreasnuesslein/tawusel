package tools
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Cancellable
import akka.actor.Scheduler
import akka.util.duration._

import models._

object Timer {
  private val system = ActorSystem("jobs")
  private var cancellable:Cancellable = null
  val exi = system.actorOf(Props(new Executor))

  def poll(dur:akka.util.FiniteDuration = 0 seconds) = {
    system.scheduler.scheduleOnce(dur ,exi, null)
  }

}

class Executor extends Actor {
  def receive = {
    case poll => {
      checkTheTime
    }
  }

  def checkTheTime = {
    try {
      var t:Tour = Tour.timerTours.get
      val difference = (t.departure.getTime() - new java.util.Date().getTime())/1000/60

      if(difference <= 30) {
        // wake up and execture tour
        t.book
        Timer.poll()
      } else {
        // sleep for X minutes, where X = time-to-next-tour - 30
        var sleep = difference - 30
        Timer.poll(sleep minutes)
      }
    } catch {
      case e => {
        // there are no tours in the future..
        //Timer.poll(3 seconds)
      }
    }

  }

}
