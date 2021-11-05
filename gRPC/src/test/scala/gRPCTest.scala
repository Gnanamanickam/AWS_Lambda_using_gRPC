import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.typesafe.config.ConfigFactory
import spray.json._

import java.util.logging.Logger
import scala.io.Source.fromURL


class gRPCTest extends AnyFlatSpec with Matchers {

  private val logger = Logger.getLogger(classOf[gRPCTest].getName)
  val port = ConfigFactory.load().getInt("port")
  val inputTime = ConfigFactory.load().getString("inputTime")
  val inputDifferentialTime = ConfigFactory.load().getString("inputDifferentialTime")
  val APIGateway = ConfigFactory.load().getString("APIGateway")

  behavior of "Configuration Parameters Module"

  // To check whether the port number is same as the one given in config file
  it should "check the port number in config" in {
    port shouldBe 50052
  }

  // To check whether the inputTime is same as the one given in config file
  it should "check the inputTime config" in {
    inputTime shouldBe "19:24:15"
  }

  // To check whether the inputDifferentialTime is same as the one given in config file
  it should "check the inputDifferentialTime config" in {
    inputDifferentialTime shouldBe "00:55:00"
  }

  "API Gateway" should "match expected value" in {

    val responseAWS = scala.io.Source.fromURL(APIGateway+inputTime+"&inputDifferentialTime="+inputDifferentialTime)
    val result = responseAWS.mkString
    val json = result.parseJson.asJsObject
    val value = json.getFields("isPresent")
    logger.info(value.toString())
    assert(value != null)
  }

}
