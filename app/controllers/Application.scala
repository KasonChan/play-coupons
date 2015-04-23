package controllers

import play.api.mvc._

object Application extends Controller {

  def login = Action {
    Ok(views.html.login())
  }

  def signup = Action {
    Ok(views.html.signup())
  }

}