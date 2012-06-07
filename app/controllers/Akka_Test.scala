package controllers
import akka.actor.Actor
import akka.actor.Props
import akka.util.duration._
import akka.actor.ActorSystem
import java.sql.Date
import java.util._
import scala.collection.mutable.HashMap
import akka.actor.Cancellable
import scala.collection.Iterator
import akka.event.Logging

case class CurrentTour(id:Int,date:Date)

object Akka_Test /*extends Actor*/{
//  val log = Logging(context.system, this)
//  log.info("akka_test")
	println("Akka_test")
	private var list: HashMap [Int,Date] = new HashMap()
	private var cancellable:Cancellable = null
	private val system = ActorSystem("jobs")
	private var curr : CurrentTour = null
	 	
//	def receive = {
//	  case "new" =>
//	}
    def insertTour(id:Int,date: Date){	  
	  
	  println("insert++++++++++++++++++"+list.size)
	  val tour = system.actorOf(Props(new Task))
	  if(list.size < 1){
	    list.put(id,date)	
	    curr = new CurrentTour(id,date)
	    cancellable = system.scheduler.scheduleOnce(1 seconds,tour,curr)
	  }
	  else
	    if(curr.date.getTime() > date.getTime()){
	      list.put(id,date)
	      curr = new CurrentTour(id,date)
	      cancellable.cancel()
	      println("canceled")
	      cancellable = system.scheduler.scheduleOnce(1 seconds,tour,curr)
	    }
//	  system.stop(tour)
	}
//    cancellable.cancel()
//    println("canceled")
	private def minTime:CurrentTour={
	  var it:Iterator[Int] = list.keySet.iterator
	  var tmp:Long = 0
	  var id:Int = 0
	  if(it.hasNext){
	    tmp = list.get(it.next).get.getTime()
	  }
	  while(it.hasNext){
	    var x = it.next
	    if(list.get(x).get.getTime() < tmp){
	      tmp = list.get(x).get.getTime()
	      id = x
	    }
	  }
	  new CurrentTour(id,list.get(id).get)
	}
	private def nextTour(){
	        if(!list.isEmpty){
        println("!list.Empty")
        curr = minTime
        val tour = system.actorOf(Props(new Task))
        cancellable = system.scheduler.scheduleOnce(1 seconds,tour,curr)
//        system.stop(tour)
      }
	}
  class Task extends Actor {
  def receive = {
    case CurrentTour(id,date)=> {
      //TODO has to send mail in a certain time and delete this tour from the list
      println(id+"+++++++++++++++++++++++++++++++++++++++++++++"+date)
      list.remove(id)
    }
    case _ => {
      println("+++++++++++++++++++++++++++++++++++++++++++++irgendwas andres")
    }
   system.stop(self); 
  }
}
}


//class CurrentTour(tid:Int,tdate:Date){
//  val id : Int= tid
//  val date : Date = tdate
//}
