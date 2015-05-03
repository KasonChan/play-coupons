package models.http

import play.api.Play.current
import play.api.libs.ws.{WS, WSResponse}

import scala.concurrent.Future

/**
 * Created by kasonchan on 5/2/15.
 */
trait HTTP {

  def getRequest(url: String): Future[WSResponse] = {
    WS.url(url).get()
  }

}
