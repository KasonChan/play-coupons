package controllers

import play.Logger
import play.api.mvc._

import scala.concurrent.Future

object Application extends Controller {

  def login = Action.async {
    Logger.info("Login")
    Future.successful(Ok("Login"))
  }

  def signup = Action.async {
    Logger.info("Signup")
    Future.successful(Ok(views.html.signup()))
  }

}