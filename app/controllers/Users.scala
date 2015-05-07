package controllers

import models.{Meta, User}
import play.api.mvc.{AnyContent, Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by kasonchan on 5/5/15.
 */
object Users extends Controller {

  /**
   * Sign in
   *
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
    val username = request.body.asFormUrlEncoded.get("email")(0)
    val password = request.body.asFormUrlEncoded.get("password")(0)

    // Create a user from the username and password
    val user = User(Some(username), Some(password), None)

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
        case m: Meta => Ok(views.html.login(m.user))
        case Some(u) => Redirect("/coupons")
        case None => Ok(views.html.login(None))
      }
    }
  }

}
