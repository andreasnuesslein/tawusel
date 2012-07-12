package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import views._

object Application extends Controller with Secured {

  def index = IsAuthenticated ({ _ => implicit request =>
     Redirect(routes.Tours.tours)
   },
   {
     Redirect(routes.Application.welcome)
   })

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

  def welcome = Action { implicit request =>
    Ok(html.welcome(loginForm))
  }
}
