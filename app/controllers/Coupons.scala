package controllers

import models.Coupon
import models.http.HTTP
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by kasonchan on 5/2/15.
 */
object Coupons extends Controller {

  def list = Action.async {
    val coupons: Future[Option[Seq[Coupon]]] =
      Coupon.findAll("http://api.bluepromocode.com/v2/promotions")

    coupons.map {
      cs => cs match {
        case Some(s) => Ok(views.html.coupons.list(s))
        case None => Ok("None")
      }
    }
  }

}
