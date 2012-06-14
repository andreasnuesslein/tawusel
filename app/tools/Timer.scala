package tools
import akka.actor.Actor
import akka.actor.Props
import akka.util.duration._
import akka.actor.ActorSystem
import java.util._
import scala.collection.mutable.HashMap
import akka.actor.Cancellable
import scala.collection.Iterator
import akka.event.Logging
import models.Tour
import java.text.SimpleDateFormat
import models.User
import notification.TourStartsSoonNotification
import tools.notification.Notification
/**
 * notifies the users in certain time before their taxi tours begin
 * */
case class CurrentTour(id:Long,date:Date)
object Timer {
	private var cancellable:Cancellable = null
	private val system = ActorSystem("jobs")
	private var curr : CurrentTour = null
	
	def notify(tr:Tour){
	  if(tr == null){
	    println("notify none")
	    curr = null
	  }
	  else {
	    println("notify else")
	    var id = tr.id
	    var date = timeFusion(tr.date,tr.departure)
	    val tour = system.actorOf(Props(new Task))
	  if(curr == null){
	    curr = new CurrentTour(id,date)
	    cancellable = system.scheduler.scheduleOnce(2 minutes,tour,curr)
	  }
	  else{
	    if(curr.date.getTime() > date.getTime()){
	      curr = new CurrentTour(id,date)
	      cancellable.cancel()
	      cancellable = system.scheduler.scheduleOnce(2 minutes,tour,curr)
	    }
	  }
	  }
	}
	
	def next(tr:Tour){
	  println("next")
	  if(tr == null){
	    println("next none")
	    curr = null
	  }
	  else
	  {
	    var id = tr.id
	    var date = timeFusion(tr.date,tr.departure)
	    val tour = system.actorOf(Props(new Task))
	    curr = new CurrentTour(id,date)
	    cancellable = system.scheduler.scheduleOnce(2 minutes,tour,curr)
	    }
	}
	def timeFusion(date:Date,time:Date):Date = {
		val df = new SimpleDateFormat( "yyyy-MM-dd" )
		val tf = new SimpleDateFormat( "HH:mm:ss" )
		val cf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" )
		cf.parse(df.format(date).toString()+" "+tf.format(time).toString())
	}
	
	class Task extends Actor {
		def receive = {
		case CurrentTour(id,date)=> {
    //TODO has to send mail in a certain time and delete this tour from the list
    var notification : Notification = null
    for(tmp : User <- Tour.getAllUsersFor(id)){
      notification = new TourStartsSoonNotification(tmp, null, Tour.findById(id))
      //DEBUG PARAMETER SET!!!
      SMS.send(notification, true)
    }
      Tour.updateCheckedByTimer(id)
      Timer.next(Tour.timerCheck)
    }
    system.stop(self)
  }
  }
}