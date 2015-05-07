package controllers

import models._
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by kasonchan on 5/5/15.
 */
object Users extends Controller {

  /**
   * Sign in
   * This function first get the username and password from the form
   * These information are then wrapped in user case class
   * Post request with the url and user case class
   * If there is an error occurs, meta will be received
   * If the user input is valid, the information of the user will be returned
   * and redirect to the coupons page
   * Otherwise, internal error is occured and show the login screen
   * @return Action[AnyContent]
   */
  def signin: Action[AnyContent] = Action.async { request =>
    // Get username and password from the form
    val username: String = request.body.asFormUrlEncoded.get("email")(0)
    val password: String = request.body.asFormUrlEncoded.get("password")(0)

    // Create a user from the username and password
    val user: User = User(Some(username), Some(password), None)

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
        case m: MetaUser => Ok(views.html.login(m.user))
        case Some(u) => Redirect("/coupons")
        case None => Ok(views.html.login(None))
      }
    }
  }

  /**
   * Signup
   * Gets fullname, username and password from the form
   * Creates signup user with the form information
   * Post request to the url with the user
   * Show the result page
   * @return Action[AnyContent]
   */
  def signup: Action[AnyContent] = Action.async { request =>
    // Get fullname, username and password from the form
    val fullname: String = request.body.asFormUrlEncoded.get("fullname")(0)
    val username: String = request.body.asFormUrlEncoded.get("email")(0)
    val password: String = request.body.asFormUrlEncoded.get("password")(0)

    val signupUser: SignupUser = SignupUser(fullname, username, password)

    val result =
      User.create("http://api.bluepromocode.com/v2/users/register", signupUser)

    result.map { r =>
      r match {
        case ms: Metas =>
          val error = Seq(ms.email, ms.password)
          Ok(views.html.signup(error))
        case Some(u) =>
          Redirect("/coupons")
        case None =>
          Ok(views.html.signup(Seq(None)))
      }
    }
  }

}
