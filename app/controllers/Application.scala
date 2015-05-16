package controllers

import models.User
import play.Logger
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

object Application extends Controller {

  /**
   * Index
   * Redirects to coupons page if session is retrieved
   * Otherwise, creates a new session and shows login page
   * Shows the login page
   * @return Action[AnyContent]
   */
  def index: Action[AnyContent] = Action.async { request =>
    request.session.get("username").map { username =>
      Logger.info("Application.login - logged in as " + username)
      Future.successful(Redirect("/coupons"))
    }.getOrElse {
      Logger.info("Application.login - new session")
      Future.successful(Ok(views.html.login(None)(User(None, None, None)))
        .withNewSession)
    }
  }

}