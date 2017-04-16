package org.monolith.bot;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GBot<N extends Notify, R extends NotifyAck> {

  private static final Logger logger = Logger.getLogger(BotClient.class.getName());

  private final ManagedChannel channel;
  private final NotifierGrpc.NotifierBlockingStub blockingStub;

  private String name;
  private int port;
  private CommandFactory<N> cmdFactory;

  public String toString() {
    return "Bot(" + this.port + ")";
  }

  public GBot(String name, int port, CommandFactory<N> cmdFactory) {

    this.name = name;
    this.port = port;
    this.cmdFactory = cmdFactory;

    this.channel = ManagedChannelBuilder.forAddress("localhost", port)
        .usePlaintext(true).build();

    this.blockingStub = NotifierGrpc.newBlockingStub(this.channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  private N command(String text) {
      return cmdFactory.make(text);
  }

  public void send(String text) {

    logger.info(this + "-> send: " + text);

    R response;

    try {
      response = (R) blockingStub.send(command(text));
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }

    logger.info("response: " + response.getMessage());
  }

}
