package org.monolith.bot;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.BindableService;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.*;

public class GBotInspector {

  private int port;
  private Server server;
  private ServerBuilder serverBuilder;
  private List<BindableService> services;
  private Thread onShutdown;
  private static final Logger logger = Logger.getLogger(GBotInspector.class.getName());

  public GBotInspector(int port) {
    this.port = port;
    this.serverBuilder = ServerBuilder.forPort(port);
    this.services = new ArrayList<BindableService>();
    this.onShutdown = new ShutdownHook();
  }

  @Override
  public String toString() {
    return "BotInspector(" + this.port + ")";
  }

  public GBotInspector addService(BindableService service) {
    this.services.add(service);
    return this;
  }

  public Server build() {
    logger.info(this + ": start");

    for (BindableService service: this.services) {
      this.serverBuilder.addService(service);
    }
    this.server = this.serverBuilder.build();
    return this.server;
  }

  public class ShutdownHook extends Thread {
    @Override
    public void run() {
      // Use stderr here since the logger may have been reset by its JVM shutdown hook.
      GBotInspector.this.stop();
      System.err.println(this + ": shut down");
    }
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  public void start() throws IOException, InterruptedException {

    this.build().start();
    Runtime.getRuntime().addShutdownHook(onShutdown);

    if (server != null) {
      server.awaitTermination();
    }
  }

}
