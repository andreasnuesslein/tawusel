package controllers

import play.api._
import play.api.mvc._

import models._

object Application extends Controller with Secured {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def secure = IsAuthenticated { username => implicit request =>
    val user = User.findByUsername(username).get
    Ok(views.html.signup.summary(user))
  }




}
