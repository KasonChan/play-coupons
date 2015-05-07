package models.http

import models.User
import play.api.Play.current
import play.api.libs.json.Json
import play.api.libs.ws.{WS, WSResponse}

import scala.concurrent.Future

/**
 * Created by kasonchan on 5/2/15.
 */
trait HTTP {

  /**
   * Get request with the url
   * @param url String
   * @return Future[WSResponse]
   */
  def getRequest(url: String): Future[WSResponse] = WS.url(url).get()

  /**
   * Post request with url and user
   * @param url String
   * @param user User
   * @return Future[WSResponse]
   */
  def postRequest(url: String, user: User): Future[WSResponse] = {
    // Create a new user
    val newUser = Json.toJson(Map("email" -> user.email.getOrElse(""),
      "password" -> user.password.getOrElse("")))

    // Post request
    WS.url(url).post(newUser)
  }

}
