package controllers

import io.swagger.annotations._
import javax.inject.Inject
import model.Link
import play.api.libs.Files
import play.api.mvc._
import services.{FileManager, LinkStorage}

import scala.concurrent.ExecutionContext

@Api("File management")
class FileController @Inject()(cc: ControllerComponents, manager: FileManager, linkStorage: LinkStorage)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {
  
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "file", required = true, paramType = "form", dataType = "file")))
  def upload: Action[MultipartFormData[Files.TemporaryFile]] = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { file =>
      handleError {
        manager.upload(file.ref.path.toFile).map(id => Ok(id.toString))
      }
    }.getOrElse(BadRequest("Missing file"))
  }

  def download(id: String) = Action {
    downloadResult(id)
  }

  def remove(id: String) = Action {
    manipulateWithId(id, parsedId =>
      handleError(manager.remove(parsedId).map {
        bool => Ok(bool.toString)
      })
    )
  }

  def link(fileId: String, expirationTs: Long): Action[AnyContent] = Action.async { request =>
    linkStorage.create(fileId, expirationTs).map {
      case Link(Some(id), _, _) => Ok(s"${request.host}/link/$id")
      case _ => InternalServerError("Something went wrong")
    }
  }

  def readLink(id: Long): Action[AnyContent] = Action.async {
    linkStorage.getNotExpiredById(id).map {
      case Some(Link(_, fileId, _)) => downloadResult(fileId) // Not redirect to not show the real file link in storage
      case None => NotFound("Link doesn't exist or has already expired")
    }
  }

  private def downloadResult(id: String) = manipulateWithId(id, parsedId =>
    handleError {
      manager.download(parsedId).map(file => Ok.sendFile(file, onClose = () => file.delete()))
    }
  )

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
