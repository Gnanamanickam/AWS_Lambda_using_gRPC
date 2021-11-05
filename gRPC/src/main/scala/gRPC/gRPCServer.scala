package gRPC

import java.util.logging.Logger
import io.grpc.{Server, ServerBuilder}
import spray.json._

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import com.typesafe.config.ConfigFactory
import gRPC.gRPCServer.APIGateway
import io.grpc.{ManagedChannel, ManagedChannelBuilder, StatusRuntimeException}
import scalapb.options.ScalapbProto.message

import com.logfile.proto.logfile.{GreeterGrpc, logReply, logRequest}
import com.logfile.proto.logfile.GreeterGrpc.GreeterBlockingStub

import scala.concurrent.{ExecutionContext, Future}

// Server that creates a sample http GET request to the connect to API Gateway and get the result from Lambda
object gRPCServer {

  private val logger = Logger.getLogger(classOf[gRPCServer].getName)
  // To get the port number from the config file
  val port = ConfigFactory.load().getInt("port")
  // To get the API URL from the config file
  val APIGateway = ConfigFactory.load().getString("APIGateway")

  // Main method to start and shutdown the server
  def main(args: Array[String]): Unit = {

    val server = new gRPCServer(ExecutionContext.global)
    // Server gets started in the given port number
    server.start()
    // Server will be in run stage until the user terminates the server manually .
    server.blockUntilShutdown()
  }
}

class gRPCServer(executionContext: ExecutionContext) { self =>

  // To start the server on the port
  val server: Server = ServerBuilder.forPort(gRPCServer.port).addService(GreeterGrpc.bindService(new GreeterImpl, executionContext)).build.start

  private def start(): Unit = {
//    server = ServerBuilder.forPort(gRPCServer.port).addService(GreeterGrpc.bindService(new GreeterImpl, executionContext)).build.start
    // Log message to say server started on the given port
    gRPCServer.logger.info("Server started on " + gRPCServer.port)
    sys.addShutdownHook {
      gRPCServer.logger.info("Shutting down gRPC server since JVM is shutting down")
      // Stop the server once JVM shutdown
      self.stop()
    }
  }

  // To Stop the server and put it in shutdown mode
  private def stop(): Unit = {
    if (server != null) {
      server.shutdown()
    }
  }

  // To block the server from getting shutdown
  private def blockUntilShutdown(): Unit = {
    if (server != null) {
      server.awaitTermination()
    }
  }

  private class GreeterImpl extends GreeterGrpc.Greeter {

    override def sayHello(req: logRequest) = {

      // Split the input into two to get input Time and Inputdifferential time to pass it separately as query param
      val input = req.name.split(",")
      val inputTime = input(0)
      val inputDifferentialTime = input(1)

      //Call Lambda API Gateway to get the response
      val responseAWS = scala.io.Source.fromURL(APIGateway+inputTime+"&inputDifferentialTime="+inputDifferentialTime)
      // Convert the result to string
      val result = responseAWS.mkString
      // Convert the string into json Object
      val json = result.parseJson.asJsObject
      // Add the json String to the reply message and send to client
      val reply = logReply(message = json.fields("isPresent").toString())
      // To close the AWS response
      responseAWS.close()
      Future.successful(reply)
    }
  }

}