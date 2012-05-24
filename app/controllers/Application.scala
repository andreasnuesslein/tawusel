package controllers

import play.api._
import play.api.mvc._

import models._

object Application extends Controller with Secured {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def secure = IsAuthenticated({ email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(views.html.signup.summary(user))
  },Redirect(routes.Auth.login).flashing("error" -> "You have to login first."))



}
