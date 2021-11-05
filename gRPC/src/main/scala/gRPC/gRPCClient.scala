package gRPC

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import com.logfile.proto.logfile.{GreeterGrpc, logRequest}
import com.logfile.proto.logfile.GreeterGrpc.GreeterBlockingStub
import io.grpc.{StatusRuntimeException, ManagedChannelBuilder, ManagedChannel}
import com.typesafe.config.ConfigFactory

object gRPCClient {

  // To get the port from the config file
  val port = ConfigFactory.load().getInt("port")
  // To get the input Time from the config file
  val inputTime = ConfigFactory.load().getString("inputTime")
  // To get the input Differential Time from the config file
  val inputDifferentialTime = ConfigFactory.load().getString("inputDifferentialTime")

  // Apply the port number and host to the blocking stub and pass it to server
  def apply(host: String, port: Int): gRPCClient = {

    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build
    val blockingStub = GreeterGrpc.blockingStub(channel)
    new gRPCClient(channel, blockingStub)
  }

  def main(args: Array[String]): Unit = {
    val client = gRPCClient("localhost", port)
    try {
      val output = args.headOption.getOrElse(inputTime + "," + inputDifferentialTime)
      client.response(output)
    } finally {
      client.shutdown()
    }
  }
}


// gRPC Client code to shut down or get the response from the server
class gRPCClient private( private val channel: ManagedChannel, private val blockingStub: GreeterBlockingStub ) {

  private[this] val logger = Logger.getLogger(classOf[gRPCClient].getName)

  // To shutdown the channel after termination
  def shutdown(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  // Method to get the response from the APi gateway in server
  def response(output: String): Unit = {
    // To request the output from the server
    val request = logRequest(name = output)
    try {
      // Get the response from the server
      val response = blockingStub.sayHello(request)
      logger.info("Time Stamp Present: " + response.message)
      // To print the message in the terminal
      response.message
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed with status code: {0}", e.getStatus)
    }
  }
}
