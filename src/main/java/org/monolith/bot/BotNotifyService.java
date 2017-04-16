package org.monolith.bot;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.logging.Logger;


public class BotNotifyService extends NotifierGrpc.NotifierImplBase {

  @Override
  public void send(Notify req, StreamObserver<NotifyAck> responseObserver) {
    NotifyAck ack = NotifyAck.newBuilder().setMessage("Notify Response: " + req.getMessage()).build();
    System.err.println("get notify: " + req.getMessage());
    responseObserver.onNext(ack);
    responseObserver.onCompleted();
  }
}
