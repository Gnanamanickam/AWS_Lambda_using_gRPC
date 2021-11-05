package controllers

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

  // To get the input time from config file
  val inputTime = ConfigFactory.load().getString("inputTime")
  // To get the input differential time from config file
  val inputDifferentialTime = ConfigFactory.load().getString("inputDifferentialTime")
  // To get the api gateway URL from the config file
  val APIGateway = ConfigFactory.load().getString("APIGateway")

  private val logger = Logger(getClass)

  /**
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def logAPI() = Action {
    implicit request: Request[AnyContent] =>

      //Logger to know the call is inside the logAPI method
      logger.trace("Inside logAPI: ")

      //Call Lambda API Gateway to get the response
      val responseAWS = scala.io.Source.fromURL(APIGateway+inputTime+"&inputDifferentialTime="+inputDifferentialTime)
      // Convert the result to string
      val result = responseAWS.mkString
      // Close the AWS call
      responseAWS.close()
      // Convert the result to JSON and send it in OK response
      Ok(Json.toJson(result))
  }
}