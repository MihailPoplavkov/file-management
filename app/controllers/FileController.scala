package controllers

import javax.inject.Inject
import play.api.libs.Files
import play.api.mvc._
import services.FileManager

import scala.concurrent.ExecutionContext

class FileController @Inject()(cc: ControllerComponents, manager: FileManager)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def upload: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { file =>
      handleError {
        manager.upload(file.ref.path.toFile).map(id => Ok(id.toString))
      }
    }.getOrElse(BadRequest("Missing file"))
  }

  def download(id: String) = Action {
    manipulateWithId(id, parsedId =>
      handleError {
        manager.download(parsedId).map(file => Ok.sendFile(file, onClose = () => file.delete()))
      }
    )
  }

  def remove(id: String) = Action {
    manipulateWithId(id, parsedId =>
      handleError(manager.remove(parsedId).map(bool => Ok(bool.toString)))
    )
  }

  private def manipulateWithId(id: String, manipulation: manager.Id => Result): Result =
    manager.parseStringToId(id) match {
      case Left(e: IllegalArgumentException) => BadRequest(s"Wrong id. ${e.getMessage}")
      case Right(parsedId) => manipulation(parsedId)
    }

  private def handleError(res: Either[Throwable, Result]): Result =
    res match {
      case Right(r) => r
      case Left(e) => InternalServerError(e.getMessage)
    }

}
