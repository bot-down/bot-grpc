package org.monolith.bot;

abstract class Transport {
  //stub
}


abstract public class Bot {

  Transport transport;

  public Bot(Transport transport) {
    this.transport = transport;
  }

  abstract void command();
  abstract void response();

}
