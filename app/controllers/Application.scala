package controllers

import models.{SignupUser, User}
import play.Logger
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

object Application extends Controller {

  /**
   * Login
   * Redirects to coupons page if session is retrieved
   * Otherwise, creates a new session and shows login page
   * Shows the login page
   * @return Action[AnyContent]
   */
  def login: Action[AnyContent] = Action { request =>
    request.session.get("connected").map { email =>
      Logger.info("Application.login - logged in as " + email)
      Redirect("/coupons")
    }.getOrElse {
      Logger.info("Application.login - new session")
      Ok(views.html.login(None)(User(None, None, None))).withNewSession
    }
  }

  /**
   * Signup
   * Creates a new session and shows signup page
   * @return Action[AnyContent]
   */
  def signup: Action[AnyContent] = Action.async {
    Logger.info("Signup")
    Future.successful(Ok(views.html.signup(Seq(None))
      (SignupUser("", "", ""))).withNewSession)
  }

  /**
   * Logout
   * Logs the user out, creates a new session and shows the login page
   * @return Action[AnyContent]
   */
  def logout: Action[AnyContent] = Action { request =>
    request.session.get("connected").map { email =>
      Logger.info("Application.logout - logged out from " + email)
      Ok(views.html.login(None)(User(None, None, None))).withNewSession
    }.getOrElse {
      Logger.info("Application.logout - logged out")
      Ok(views.html.login(None)(User(None, None, None))).withNewSession
    }
  }

}