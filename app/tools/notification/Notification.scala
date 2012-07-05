package tools.notification

import models._

abstract class Notification(
  val notifiedUser: User,
  val interactingUser: User,
  val tour: Tour
  ) {
  //abstract members
  val subject: String
  val text: String
  val shortText: String

}

