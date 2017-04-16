package org.monolith.bot;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class BotServer {
  private static final Logger logger = Logger.getLogger(BotServer.class.getName());

  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = ServerBuilder.forPort(port)
        .addService(new NotifierImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        BotServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final BotServer server = new BotServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class NotifierImpl extends NotifierGrpc.NotifierImplBase {

    @Override
    public void send(Notify req, StreamObserver<NotifyAck> responseObserver) {
      NotifyAck ack = NotifyAck.newBuilder().setMessage("Notify Response: " + req.getMessage()).build();
      System.err.println("get notify: " + req.getMessage());
      responseObserver.onNext(ack);
      responseObserver.onCompleted();
    }
  }
}
