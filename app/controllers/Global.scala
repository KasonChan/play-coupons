package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.Future

/**
 * Created by kasonchan on 5/6/15.
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) =
    Logger.info("Application has started")


  override def onStop(app: Application) =
    Logger.info("Application shutdown...")


  // 404 - page not found error
  override def onHandlerNotFound(request: RequestHeader) =
    Future.successful(NotFound(views.html.global.notFound(Some(request.path))))


  // 500 - internal server error
  override def onError(request: RequestHeader, throwable: Throwable) =
    Future.successful(InternalServerError(views.html.global.errors(Some(throwable))))

}
