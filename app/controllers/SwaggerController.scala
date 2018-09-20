package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class SwaggerController @Inject()(cc: ControllerComponents)
                                 (implicit ec: ExecutionContext) extends AbstractController(cc) {

  def swagger = Action {
    Redirect(url = "/assets/lib/swagger-ui/index.html", queryString = Map("url" -> Seq("/swagger.json")))
  }
}
