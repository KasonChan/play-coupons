package controllers

import play.Logger
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
  override def onHandlerNotFound(request: RequestHeader) = {
    request.session.get("username").map { username =>
      Logger.info("Global.onHandlerNotFound - " + username)
      Future.successful(NotFound(views.html.global.notFound(Some(username))(Some(request.path))))
    }.getOrElse {
      Logger.info("Global.onHandlerNotFound - new session")
      Future.successful(NotFound(views.html.global.notFound(None)(Some(request.path))).withNewSession)
    }
  }

  /**
   * On Error
   * Handles 500 internal server error
   * @param request RequestHeader
   * @param throwable Throwable
   * @return Future[SimpleResult]
   */
  override def onError(request: RequestHeader, throwable: Throwable) = {
    request.session.get("username").map { username =>
      Logger.info("Global.onError - " + username)
      Future.successful(InternalServerError(views.html.global.errors(Some(username))(Some(throwable))))
    }.getOrElse {
      Logger.info("Global.onError - new session")
      Future.successful(InternalServerError(views.html.global.errors(None)(Some(throwable))).withNewSession)
    }
  }

}
