package Rest

import com.typesafe.config.ConfigFactory

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json
import play.api.Logger

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class restClient @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController {

  val inputTime = ConfigFactory.load().getString("inputTime")
  val inputDifferentialTime = ConfigFactory.load().getString("inputDifferentialTime")

  private val logger = Logger(getClass)

  /**
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def logAPI() = Action {
    implicit request: Request[AnyContent] =>

      logger.trace("Inside logAPI: ")

      //Call Lambda API Gateway
      val responseAWS = scala.io.Source.fromURL("https://rjsduxe26m.execute-api.us-east-1.amazonaws.com/Prod/hello?inputTime="+inputTime+"&inputDifferentialTime="+inputDifferentialTime)
      val result = responseAWS.mkString
      responseAWS.close()
      Ok(Json.toJson(result))
  }
}