package controllers

import play.api._
import play.api.mvc._

import models._

object Tours extends Controller with Secured {

  def newTour = IsAuthenticated({ email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(views.html.start())
  }, Redirect(routes.Application.index))

  def tours = IsAuthenticated({ email => implicit request =>
    Ok(views.html.tours())
  })

}
