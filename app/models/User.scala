package models

import models.http.HTTP
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.api.libs.ws.WSResponse
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by kasonchan on 5/5/15.
 */
case class User(email: Option[String],
                password: Option[String],
                username: Option[String])

case class Meta(error: Option[Boolean],
                code: Option[Int],
                user: Option[String])

case class SignupUser(fullname: String,
                      email: String,
                      password: String)

object User extends Controller with HTTP {

  /**
   * Username
   * Retrieves username
   * @param u JsObject
   * @return Option[String]
   */
  private def username(u: JsObject): Option[String] = (u \ "username").asOpt[String]

  /**
   * Email
   * Retrieves email
   * @param u JsObject
   * @return Option[String]
   */
  private def email(u: JsObject): Option[String] = (u \ "email").asOpt[String]

  /**
   * Error
   * Retrieves errors
   * @param m JsObject
   * @return Option[Boolean]
   */
  private def error(m: JsObject): Option[Boolean] = (m \ "error").asOpt[Boolean]

  /**
   * Code
   * Retrieves codes
   * @param m JsObject
   * @return Option[Int]
   */
  private def code(m: JsObject): Option[Int] = (m \ "code").asOpt[Int]

  /**
   * User
   * Retrieves user
   * @param m JsObject
   * @return Option[String]
   */
  private def user(m: JsObject): Option[String] = (m \ "user").asOpt[String]

  /**
   * Meta
   * Retrieves meta data including error, code and user
   * @param m JsObject
   * @return Meta
   */
  private def meta(m: JsObject): Meta = Meta(error(m), code(m), user(m))

  /**
   * Find
   * Performs post request with url and user
   * If the user is valid, it will return the information of the user
   * If the user is invalid, it will return a meta data
   * Otherwise it will return none
   * @param url String
   * @param user User
   * @return Future[Product with Serializable]
   */
  def find(url: String, user: User): Future[Product with Serializable] = {
    val apiFuture: Future[WSResponse] =
      postRequest(url, user)

    apiFuture.map { response =>

      // Parse the response body to json
      val json: JsValue = Json.parse(response.body)

      // Retrieve meta json object
      val metaJsObject: Option[JsObject] = (json \ "meta").asOpt[JsObject]

      // If meta error is retrieved, return it
      // Otherwise retrieve and return a user
      metaJsObject match {

        case Some(m) =>
          meta(m)
        case None =>
          // Retrieve the users json array
          val usersJsArray: Option[JsArray] = (json \ "users").asOpt[JsArray]

          usersJsArray match {
            case Some(uja: JsArray) =>

              val usersJsObjects: Option[Seq[JsObject]] =
                (json \ "users").asOpt[Seq[JsObject]]

              usersJsObjects match {
                case Some(userJsObject) =>
                  val u = userJsObject.map { u =>
                    User(email(u),
                      user.password,
                      username(u))
                  }

                  Some(u)
                case None =>
                  None
              }
            case None =>
              None
          }
      }
    }
  }

}
