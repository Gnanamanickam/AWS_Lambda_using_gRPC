package gRPC

import java.util.concurrent.TimeUnit
import java.util.logging.{Level, Logger}

import com.logfile.proto.logfile.{GreeterGrpc, logRequest}
import com.logfile.proto.logfile.GreeterGrpc.GreeterBlockingStub
import io.grpc.{StatusRuntimeException, ManagedChannelBuilder, ManagedChannel}
import com.typesafe.config.ConfigFactory

object gRPCClient {

  val port = ConfigFactory.load().getInt("port")

  def apply(host: String, port: Int): gRPCClient = {

    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build
    val blockingStub = GreeterGrpc.blockingStub(channel)
    new gRPCClient(channel, blockingStub)
  }

  def main(args: Array[String]): Unit = {
    val client = gRPCClient("localhost", port)
    try {
      val output = args.headOption.getOrElse("19:24:15,06:00:00")
      client.response(output)
    } finally {
      client.shutdown()
    }
  }
}

class gRPCClient private( private val channel: ManagedChannel, private val blockingStub: GreeterBlockingStub ) {

  private[this] val logger = Logger.getLogger(classOf[gRPCClient].getName)

  def shutdown(): Unit = {
    channel.shutdown.awaitTermination(5, TimeUnit.SECONDS)
  }

  def response(output: String): Unit = {
    val request = logRequest(name = output)
    try {
      val response = blockingStub.sayHello(request)
      logger.info("Greeting: " + response.message)
      response.message
    }
    catch {
      case e: StatusRuntimeException =>
        logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus)
    }
  }
}
