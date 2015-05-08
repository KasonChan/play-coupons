package controllers

import models.User
import play.Logger
import play.api.mvc.{Action, AnyContent, Controller}

object Application extends Controller {

  /**
   * Index
   * Redirects to coupons page if session is retrieved
   * Otherwise, creates a new session and shows login page
   * Shows the login page
   * @return Action[AnyContent]
   */
  def index: Action[AnyContent] = Action { request =>
    request.session.get("connected").map { email =>
      Logger.info("Application.login - logged in as " + email)
      Redirect("/coupons")
    }.getOrElse {
      Logger.info("Application.login - new session")
      Ok(views.html.login(None)(User(None, None, None))).withNewSession
    }
  }

}