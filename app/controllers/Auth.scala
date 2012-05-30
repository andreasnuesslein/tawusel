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


/**
 * 
 */
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
      "email" -> email.verifying("This email adress is already in use", User.findByEmail(_).isEmpty),
      "firstname" -> text(minLength = 2, maxLength = 45),
      "lastname" -> text(minLength = 2, maxLength = 45),
      "cellphone" -> text(minLength = 10).verifying("Choose a valid cellphone number", _.matches(cellphonePattern)),
      "password" -> tuple(
            "main" -> text(minLength = 6),
            "confirm" -> text
      ).verifying(
        // Add an additional constraint: both passwords must match
        "Passwords don't match", passwords => passwords._1 == passwords._2
      )
    ) { (email, firstname, lastname, cellphone, passwords)
      => User(email, firstname, lastname, cellphone, passwords._1)
    }
    {
      user => Some(user.email, user.firstname, user.lastname, user.cellphone, (user.password, ""))
    }
  )

  /**
   * Implementation of the login method (action) which
   * routes the user to the index.html if it is loged in and to the 
   * login.html he is not.
   */
  def login = Action { implicit request =>
      session.get("email") match {
        case Some(_) => Redirect(routes.Application.index).flashing(
          "info" -> "You're already logged in." )
        case None => Ok(html.auth.login(loginForm))
      }
  }
  /**
   * Implementation of the authenticate method (action) which shows
   * the login formular with errors if they exists and redirects the
   * user to the index.html otherwise.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.auth.login(formWithErrors)),
      email => {
        var user = User.findByEmail(email._1).get
        Redirect(routes.Tours.newTour).withSession("email" -> user.email, "firstname" -> user.firstname)
    }
    )
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
      User.create(user)
      Redirect(routes.Application.index).withSession("email" -> user.email,"firstname" -> user.firstname)
    }
   )
  }


  /**
   * Implementation of the logout method (action).
   */
  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  def account = IsAuthenticated { email => implicit request =>
    val user = User.findByEmail(email).get
    Ok(html.auth.summary(user))
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
