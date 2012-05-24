package controllers

import models._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data._
import play.api.mvc._
import play.api._
import views._


object Auth extends Controller with Secured {
  
  val loginForm = Form(
    tuple(
      "email" -> email,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
      })
  )
  
  val regForm: Form[User] = Form(
    mapping(
      "email" -> email,
      "forename" -> text,
      "lastname" -> text,
      "cellphone" -> text,
      "password" -> tuple(
            "main" -> text(minLength = 6),
            "confirm" -> text
          ).verifying(
            // Add an additional constraint: both passwords must match
              "Passwords don't match", passwords => passwords._1 == passwords._2
            )
      
  ) {
     (email, forename, lastname, cellphone, passwords) 
     => User(email, forename, lastname, cellphone, passwords._1)
    }
    {
      user => Some(user.email, user.forename, user.lastname, user.cellphone, (user.password, ""))
    }.verifying(
      "This email is not available",
      user => User.findByEmail(user.email).isEmpty
      )
  )

  def login = Action { implicit request =>
      session.get("email") match {
        case Some(_) => Redirect(routes.Application.index).flashing(
          "success" -> "You're already logged in" )
        case None => Ok(html.login(loginForm))
      }
  }

  def register = Action { implicit request =>
    try {
    	Ok(html.signup.form(regForm))
    } catch {
   		case e => Ok(html.signup.form(regForm))
    }
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("email" -> user._1)
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
      Redirect(routes.Application.index).withSession("email" -> user.email)
    }
   )
  }
}

trait Secured {
  private def email(request: RequestHeader) = request.session.get("email")
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(email, onUnauthorized) { user =>
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


