package tools.notification

import models._

abstract class Notification(
  notifiedUser: User, 
  interactingUser: User, 
  tour: Tour 
  ) {
  //abstract members
  val subject: String
  val text: String 
  val shortText: String
  
  //getter
  def getNotifiedUser: User = notifiedUser
  def getInteractingUser: User = interactingUser
  def getTour: Tour = tour
  def getSubject: String = subject
  def getText: String = text
  def getShortText: String = shortText
}
	
