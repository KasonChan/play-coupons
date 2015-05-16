package controllers

import models._
import play.Logger
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.ws.WSResponse
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by kasonchan on 5/5/15.
 */
object Users extends Controller {

  /**
   * Sign in
   * This function first get the email and password from the form
   * These information are then wrapped in user case class
   * Post request with the url and user case class
   * If there is an error occurs, meta will be received and new session will be
   * created
   * If the user input is valid, the information of the user will be returned
   * and redirect to the coupons page
   * Otherwise, internal error is occured and show the login screen with new
   * session
   * @return Action[AnyContent]
   */
  def signin: Action[AnyContent] = Action.async { request =>
    // Get email and password from the form
    val email: String = request.body.asFormUrlEncoded.get("email")(0)
    val password: String = request.body.asFormUrlEncoded.get("password")(0)

    // Create a user from the email and password
    val user: User = User(Some(email), Some(password), None)

    // Post request using the user
    val result =
      User.find("http://api.bluepromocode.com/v2/users/login", user)

    // If an error occurs (invalid user credit or not found), the meta will be
    // returned
    // If the user input is valid, the user info is returned and redirect to
    // coupons page
    // Otherwise, internal error occurs, login screen will be shown
    result.map { r =>
      r match {
        case m: MetaUser =>
          Logger.info("Users.signin - new session - " + m.user.getOrElse(""))
          Ok(views.html.login(m.user)
            (User(Some(email), Some(password), None))).withNewSession
        case Some((u: Seq[User], r: WSResponse)) => {
          val username = u(0).username.getOrElse("")

          println(r.allHeaders.toString)
          Cache.set("headers", r.allHeaders)
          Logger.info("Users.signin - logged in as " + username)

          Redirect("/coupons").withSession(
            "email" -> email,
            "password" -> password,
            "username" -> username)
        }
        case None =>
          Logger.error("Users.signin - new session - internal server error")
          Ok(views.html.login(None)
            (User(Some(email), Some(password), None))).withNewSession
      }
    }
  }

  /**
   * Create
   * Gets fullname, email and password from the form
   * Creates signup user with the form information
   * Post request to the url with the user
   * Shows the signup page with new session if sign up input is valid
   * Otherwise shows the signup page with new session
   * @return Action[AnyContent]
   */
  def create: Action[AnyContent] = Action.async { request =>
    // Get fullname, email and password from the form
    val fullname: String = request.body.asFormUrlEncoded.get("fullname")(0)
    val email: String = request.body.asFormUrlEncoded.get("email")(0)
    val password: String = request.body.asFormUrlEncoded.get("password")(0)

    val signupUser: SignupUser = SignupUser(fullname, email, password)

    val result =
      User.create("http://api.bluepromocode.com/v2/users/register", signupUser)

    result.map { r =>
      r match {
        case ms: Metas =>
          Logger.info("Users.signup - new session - " + ms.email + " " +
            ms.password)
          val error = Seq(ms.email, ms.password)
          Ok(views.html.signup(error)(signupUser)).withNewSession
        case Some((u: Seq[User], r: WSResponse)) => {
          val username = u(0).username.getOrElse("")

          println(r.allHeaders.toString)
          Cache.set("headers", r.allHeaders)
          Logger.info("Users.signup - Signed up as " + email)

          Redirect("/coupons").withSession(
            "email" -> email,
            "password" -> password,
            "username" -> username)
        }
        case None =>
          Logger.error("Users.signup - new session - internal server error")
          Ok(views.html.signup(Seq(None))(signupUser)).withNewSession
      }
    }
  }

  /**
   * Signup
   * Creates a new session and shows signup page
   * @return Action[AnyContent]
   */
  def signup: Action[AnyContent] = Action.async {
    Logger.info("Signup")
    Future.successful(Ok(views.html.signup(Seq(None))(SignupUser("", "", ""))).withNewSession)
  }

  /**
   * Logout
   * Logs the user out, creates a new session and shows the login page
   * @return Action[AnyContent]
   */
  def logout: Action[AnyContent] = Action { request =>
    request.session.get("username").map { username =>
      Logger.info("Application.logout - logged out from " + username)
      Ok(views.html.login(None)(User(None, None, None))).withNewSession
    }.getOrElse {
      Logger.info("Application.logout")
      Ok(views.html.login(None)(User(None, None, None))).withNewSession
    }
  }

}