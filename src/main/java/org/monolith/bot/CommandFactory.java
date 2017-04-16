package org.monolith.bot;


public interface CommandFactory<N> {
  N make(String text);
}
