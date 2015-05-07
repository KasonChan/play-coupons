package controllers

import play.Logger
import play.api.mvc._

import scala.concurrent.Future

object Application extends Controller {

  /**
   * Login
   *
   * Shows the login page
   *
   * @return Action[AnyContent]
   */
  def login: Action[AnyContent] = Action.async {
    Logger.info("Login")
    Future.successful(Ok(views.html.login(None)))
  }

  /**
   * Signup
   *
   * Shows the sign up page
   *
   * @return Action[AnyContent]
   */
  def signup: Action[AnyContent] = Action.async {
    Logger.info("Signup")
    Future.successful(Ok(views.html.signup(None)))
  }

}