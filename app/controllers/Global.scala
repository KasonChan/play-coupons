package controllers

import play.api._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future

/**
 * Created by kasonchan on 5/6/15.
 */
object Global extends GlobalSettings {

  /**
   * On start
   * Logs the start of the app
   * @param app Application
   */
  override def onStart(app: Application) =
    Logger.info("Application has started")


  /**
   * On stop
   * Logs the stop of the app
   * @param app Application
   */
  override def onStop(app: Application) =
    Logger.info("Application shutdown...")

  /**
   * On handler not found
   * Handles 404 page not found error
   * @param request RequestHeader
   * @return Future[SimpleResult]
   */
  override def onHandlerNotFound(request: RequestHeader) =
    Future.successful(NotFound(views.html.global.notFound(Some(request.path))))

  /**
   * On Error
   * Handles 500 internal server error
   * @param request RequestHeader
   * @param throwable Throwable
   * @return Future[SimpleResult]
   */
  override def onError(request: RequestHeader, throwable: Throwable) =
    Future.successful(InternalServerError(views.html.global.errors(Some(throwable))))

}
