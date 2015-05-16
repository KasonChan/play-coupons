package models.http

import java.nio.Buffer

import models.{SignupUser, User}
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.json.Json
import play.api.libs.ws.{WSAuthScheme, WS, WSResponse}

import scala.collection.mutable
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
   * Get request with auth, url and user
   * @param url String
   * @param user User
   * @return Future[WSResponse]
   */
  def getRequestPersonalized(url: String, user: User): Future[WSResponse] = {
    WS.url(url).withHeaders("email" -> user.email.getOrElse(""),
      "password" -> user.password.getOrElse("")).get()

    WS.url(url).withAuth(user.email.getOrElse(""), user.password.getOrElse(""), WSAuthScheme.DIGEST).get()
  }

  /**
   * Post request with url and user for finding if the user is valid
   * @param url String
   * @param user User
   * @return Future[WSResponse]
   */
  def postRequestFind(url: String, user: User): Future[WSResponse] = {
    // Create a user for finding
    val findUser = Json.toJson(Map("email" -> user.email.getOrElse(""),
      "password" -> user.password.getOrElse("")))

    // Post request
    WS.url(url).post(findUser)
  }

  /**
   * Post request with url and user for creating a new user
   * @param url String
   * @param user SignupUser
   * @return Future[WSResponse]
   */
  def postRequestCreate(url: String, user: SignupUser): Future[WSResponse] = {
    // Create a new user
    val createUser = Json.toJson(Map("fullName" -> user.fullname,
      "email" -> user.email,
      "password" -> user.password))

    // Post request
    WS.url(url).post(createUser)
  }

}
