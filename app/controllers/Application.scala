package controllers

import play.api.Play.current
import play.api.libs.json._
import play.api.libs.ws.{WS, WSResponse}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Savings(percentOff: Option[Double], amountOff: Option[Double], types: Option[List[String]])

case class Coupons()

object Application extends Controller {

  def login = Action.async {

    val apiFuture: Future[WSResponse] =
      WS.url("http://api.bluepromocode.com/v2/promotions").get()

    apiFuture.map {
      response => {
        val json: JsValue = Json.parse(response.body)

        val promotions: Option[JsArray] = (json \ "promotions").asOpt[JsArray]

        promotions match {
          case Some(ps: JsArray) =>
            val coupons: Option[Vector[JsObject]] = (json \ "promotions").asOpt[Vector[JsObject]]

            coupons match {
              case Some(coupon) =>

                val c = coupon.map {
                  c =>
                    ((c \ "savings").as[JsObject],
                    (c \ "merchant").as[JsObject])
                }

                Ok(c.toString)
              case None =>
                Ok("No coupons")
            }
          case None =>
            Ok("No promotions")
        }
      }
    }
  }

  def signup = Action.async {
    Future.successful(Ok(views.html.signup()))
  }

}