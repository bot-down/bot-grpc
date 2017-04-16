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

  public static void main(String[] args) throws IOException, InterruptedException {
    final GBotInspector mainBot = new GBotInspector(50051);

    mainBot.addService(new BotNotifyService())
      .start();
  }
}
