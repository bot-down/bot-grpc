package org.monolith.bot;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BotClient {
  private static final Logger logger = Logger.getLogger(BotClient.class.getName());

  private final ManagedChannel channel;
  private final NotifierGrpc.NotifierBlockingStub blockingStub;

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public BotClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext(true));
  }

  /** Construct client for accessing RouteGuide server using the existing channel. */
  BotClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = NotifierGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public void send(String text) {
    logger.info("Send notify: " + text + " ...");
    Notify request = Notify.newBuilder().setMessage(text).build();
    NotifyAck response;
    try {
      response = blockingStub.send(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("response: " + response.getMessage());
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {

    String clientName;
    if (args.length > 0) {
      clientName = args[0];
    } else {
      clientName = "client";
    }

    new Thread(() -> {

      final BotClient client = new BotClient("localhost", 50051);

      logger.log(Level.INFO, "thread start");
      try {
        while (true) {

          try {
            /* Access a service running on the local machine on port 50051 */
            String notify = clientName + ": " + System.currentTimeMillis();
            client.send(notify);
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "thread down");
          }
        }
      } catch (Exception e) {
        logger.log(Level.SEVERE, "thread down");
      } finally {
        logger.log(Level.SEVERE, "client shutdown");
        try {
          client.shutdown();
        } catch (InterruptedException e) {
          logger.log(Level.SEVERE, "client not shutdown");
        }
      }

    }).start();

    for (;;);
  }
}
