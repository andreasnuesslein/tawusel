package tools

import com.typesafe.plugin._
import play.api.Play.current

import tools.notification.Notification

object Mail {

    def send(notification: Notification) {
      val mail = use[MailerPlugin].email
      mail.setSubject(notification.subject)
      mail.addRecipient(notification.notifiedUser.email)
      mail.addFrom("tawusel@dev.noova.de")
      mail.sendHtml(notification.text)
    }
}
