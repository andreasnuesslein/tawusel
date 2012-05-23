package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models._
import views._


object Auth extends Controller with Secured {
  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
      })
  )
  val regForm: Form[User] = Form(
    mapping(
      "username" -> text(minLength = 4),
      "first_name" -> text,
      "last_name" -> text,
      "email" -> email,
      "password" -> tuple(
            "main" -> text(minLength = 6),
            "confirm" -> text
          ).verifying(
            // Add an additional constraint: both passwords must match
              "Passwords don't match", passwords => passwords._1 == passwords._2
            ),
      "cellphone" -> optional(text),
      "homephone" -> optional(text),
      "street" -> optional(text),
      "zip" -> optional(text),
      "city" -> optional(text),
      "notes" -> optional(text)
  ) {
     (username, first_name, last_name, email, passwords, cellphone, homephone, street, zip, city,
       notes) => User(username, first_name, last_name, email, passwords._1, cellphone, homephone, street, zip, city, notes)
    }
    {
      user => Some(user.username, user.first_name, user.last_name, user.email, (user.password, ""),
        user.cellphone, user.homephone, user.street, user.zip, user.city, user.notes)
    }.verifying(
      "This username is not available",
      user => !Seq("admin", "guest").contains(user.username)
      )
  )

  def login = Action { implicit request =>
      session.get("username") match {
        case Some(_) => Redirect(routes.Application.index).flashing(
          "success" -> "You're already logged in" )
        case None => Ok(html.login(loginForm))
      }
  }

  def register = Action { implicit request =>
    Ok(html.signup.form(regForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("username" -> user._1)
    )
  }

  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def account = TODO
  def submit = Action { implicit request =>
   regForm.bindFromRequest.fold(
     formWithErrors => {
       println(formWithErrors)
       BadRequest(html.signup.form(formWithErrors))
     },
     user => {
      User.create(user)
      Redirect(routes.Application.index).withSession("username" -> user.username)
    }
   )
  }


}

trait Secured {
  private def username(request: RequestHeader) = request.session.get("username")
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

  /*def IsMemberOf(project: Long)(f: => String => Request[AnyContent] => Result) = IsAuthenticated { user => request =>
    if(Project.isMember(project, user)) {
      f(user)(request)
    } else {
      Results.Forbidden
    }
  }*/

}


