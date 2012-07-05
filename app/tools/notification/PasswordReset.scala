package tools.notification

import play.api.Configuration
import models._
import play.api.Play.current


class PasswordReset(
  notifiedUser: User,
  interactingUser: User,
  tour: Tour) extends Notification(notifiedUser, interactingUser,tour) {

  val token = notifiedUser.passwordResetToken

  val subject = "TaWusel Password Assistance"

  val text = """We received a request to reset the password associated with this e-mail address.<br />
  If you made this request, please follow the instructions below.<br />
  <br />
  Click the link below to reset your password:<br />
  %s<br />
  <br />
  If you did not request to have your password reset you can safely ignore this email. Rest assured your customer account is safe.<br />
  <br />
  Best regards,<br />
  TaWusel
  """.format("http://"+current.configuration.getString("host.name").get+"/password/reset/"+notifiedUser.email+"/"+token)

  val shortText = ""

}
