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
case class User(email: Option[String], password: Option[String], username: Option[String])

case class Meta(error: Option[Boolean], code: Option[Int], user: Option[String])

object User extends Controller with HTTP {

  /**
   * Find
   *
   * Performs post request with url and user
   * If the user is valid, it will return the information of the user
   * If the user is invalid, it will return a meta data
   * Otherwise it will return none
   *
   * @param url
   * @param user
   * @return
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

  private def username(u: JsObject): Option[String] = (u \ "username").asOpt[String]

  private def email(u: JsObject): Option[String] = (u \ "email").asOpt[String]

  private def error(m: JsObject): Option[Boolean] = (m \ "error").asOpt[Boolean]

  private def code(m: JsObject): Option[Int] = (m \ "code").asOpt[Int]

  private def user(m: JsObject): Option[String] = (m \ "user").asOpt[String]

  private def meta(m: JsObject): Meta = Meta(error(m), code(m), user(m))

}