package org.monolith.bot;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BotClient {

  private static final Logger logger = Logger.getLogger(GBotInspector.class.getName());

  public static class NotifyFactory implements CommandFactory<Notify> {
    public Notify make(String text) {
      return Notify.newBuilder().setMessage(text).build();
    }

  }

  public static void main(String[] args) throws Exception {

    String botName;
    if (args.length > 0) {
      botName = args[0];
    } else {
      botName = "bot";
    }

    new Thread(() -> {

      final GBot<Notify, NotifyAck> bot = new GBot<>(botName, 50051, new NotifyFactory());

      try {
        while (true) {

          try {
            /* Access a service running on the local machine on port 50051 */
            String notify = botName + ": " + System.currentTimeMillis();
            bot.send(notify);
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
          bot.shutdown();
        } catch (InterruptedException e) {
          logger.log(Level.SEVERE, "client not shutdown");
        }
      }

    }).start();

    for (;;);
  }
}
