package controllers

import play.data.validation._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data._
import play.api.mvc._
import play.api._
import scala.util.matching.Regex
import models._
import views._
import tools.Mail
import tools.notification._

object Auth extends Controller with Secured {

  /**
   * Definition of the login formular as a variable including the
   * verifying request if a user want's to log in.
   */
  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
        //call the authenticate method of the user model to query the actual verifying
        case (email, password) => User.authenticate(email, password).isDefined
      }
      )
  )

  val cellphonePattern = "((^\\(\\+?\\d+[\\s]*\\d*\\)|^\\(\\d+\\)|^\\+?\\d+|^\\d+)+([\\-\\/\\s])*(\\d)+)"

  /**
   * Definition of the registration formular as a variable including the verifying for
   * some simple cases like email and cellphone and the mapping to an user object of the
   * user input.
   */
  val registrationForm: Form[User] = Form(
    mapping(
      "email" -> email.verifying("This email address is already in use", User.findByEmail(_).isEmpty),
      "firstname" -> text(minLength = 2, maxLength = 45),
      "lastname" -> text(minLength = 2, maxLength = 45),
      "cellphone" -> text(minLength = 10).verifying("Choose a valid mobile phone number", _.matches(cellphonePattern)),
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text
      ).verifying(
          // Add an additional constraint: both passwords must match
          "Passwords don't match", passwords => passwords._1 == passwords._2
        )
    ) { (email, firstname, lastname, cellphone, passwords) => User(-1, email, firstname, lastname, cellphone, passwords._1, null)
      } {
        user => Some(user.email, user.firstname, user.lastname, user.cellphone, (user.password, ""))
      }
  )

  /**
   * Implementation of the login method (action) which
   * routes the user to the index.html if it is loged in and to the
   * login.html he is not.
   */
  def login = Action { implicit request =>
    if (request.method == "GET") {
      session.get("email") match {
        case Some(_) => Redirect(routes.Tours.tours).flashing(
          "info" -> "You're already logged in.")
        case None => Ok(html.auth.login(loginForm))
      }
    }
    else {
      loginForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.auth.login(formWithErrors)),
        email => {
          var user = User.findByEmail(email._1).get
          Redirect(routes.Tours.tours).withSession("email" -> user.email, "firstname" -> user.firstname)
        }
      )
    }
  }

  /**
   * Implementation of the register method (action) which
   * contains just a redirect to the signup.html.
   */
  def register = Action { implicit request =>
    Ok(html.auth.register(registrationForm))
  }
  def submit = Action { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(html.auth.register(formWithErrors))
      },
      user => {
        user.create
        Mail.send(new RegisterNotification(user, null, null))
        Redirect(routes.Tours.tours).withSession("email" -> user.email, "firstname" -> user.firstname)
      }
    )
  }

  def registerPerApp = Action { implicit request =>
    println(request.body.asFormUrlEncoded.get)
    var x = request.body.asFormUrlEncoded.get
    var json = null;
    val userId = User.create(x("email").first, x("firstname").first, x("lastname").first, x("phone").first, x("password").first);
    if (userId != 0) {
      Ok("success")
    }
    else {
      Ok("registration failed, user exists already")
    }
  }

  def updateNotifications = IsAuthenticated { email =>
    implicit request =>
      val user = User.findByEmail(email).get
      var x = request.body.asFormUrlEncoded.get
      UserNotification.update(user.id, x.contains("sbdy_joined_email"),
        x.contains("sbdy_joined_sms"), x.contains("sbdy_left_email"),
        x.contains("sbdy_left_sms"), x.contains("xmin_email"), x.contains("xmin_sms"),
        x.contains("must_call_email"), x.contains("must_call_sms"),
        x.contains("status_changed_email"), x.contains("status_changed_sms"))
      Redirect(routes.Auth.account).flashing(
        "success" -> "Notifications have been updated")
  }

  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You've been logged out."
    )
  }

  val editForm = Form(
    tuple(
      "email" -> email,
      "cellphone" -> text,
      "extension" -> optional(text)
    )
  )

  def account = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    val notifications = UserNotification.getForUser(user.id)
    val history = Tour.getHistoryForUser(user.id)
    val favorites = Tour.getFavoritesForUser(user.id)
    Ok(html.auth.profile(user, editForm.fill((user.email,user.cellphone,user.extension)), notifications, history, favorites))
  }

  def editUser = IsAuthenticated { email =>
    implicit request =>
      val user = User.findByEmail(email).get
      val x = request.body.asFormUrlEncoded.get
      user.update(x("email").first, x("cellphone").first, Option(x("extension").first))
      Redirect(routes.Auth.account).withNewSession.withSession("email" -> x("email").first, "firstname" -> user.firstname)
        .flashing("success" -> "Successfully updated.")
  }

  def passwordReset(email: String, token: String) = Action { implicit request =>
    val user = User.findByEmail(email).get
    if (user.checkResetToken(token)) {
      Redirect(routes.Auth.passwordChange).withSession("email" -> user.email, "firstname" -> user.firstname)
    }
    else {
      BadRequest("Invalid token. This might have happened because you have waited to long to click the password reset link or you already resetted your password.")
    }
  }

  val passForm = Form(
    tuple(
      "pwmain" -> text(minLength = 6),
      "pwconfirm" -> text
    ).verifying(
        "Passwords don't match", passwords => passwords._1 == passwords._2
      )
  )

  def passwordChange = IsAuthenticated { email =>
    implicit request =>
      if (request.method == "GET") {
        Ok(html.auth.passchange(passForm))
      }
      else {
        passForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(html.auth.passchange(formWithErrors))
          },
          pw => {
            val user = User.findByEmail(email).get
            user.changePassword(pw._1)
            Redirect(routes.Tours.tours).flashing("success" -> "Changed pw")
            //Redirect(routes.Tours.tours).withSession("email" -> user.email,"firstname" -> user.firstname)
          }
        )
      }

  }

  def passwordLost = Action { implicit request =>
    if (request.method == "GET") {
      Ok(html.auth.passlost(loginForm))
    }
    else {
      val email = request.body.asFormUrlEncoded.get.get("email").get(0)
      println(email)
      try {
        val user = User.findByEmail(email).get
        Mail.send(new PasswordReset(user, null, null))
      }
      catch { case _ => "x" }
      Redirect(routes.Auth.login).flashing("info" -> """If the e-mail address you entered (%s) is associated
        with a customer account in our records, you will receive an e-mail from us with instructions for resetting
        your password.""".format(email))
    }
  }

}

trait Secured {
  private def username(request: RequestHeader) = request.session.get("email")
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  def IsAuthenticated(f: => String => Request[AnyContent] => Result) =
    Security.Authenticated(username, onUnauthorized) { user => Action(request => f(user)(request))
    }

  def IsAuthenticated(f: => String => Request[AnyContent] => Result, g: => Result) =
    Security.Authenticated(username, RequestHeader => g) { user => Action(request => f(user)(request))
    }

}
