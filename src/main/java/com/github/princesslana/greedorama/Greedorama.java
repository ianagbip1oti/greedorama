package com.github.princesslana.greedorama;

import disparse.discord.smalld.Dispatcher;

public class Greedorama {

  public static void main(String[] args) {
    Config.getDatabase().initialize();

    var disparse =
        new Dispatcher.Builder(Greedorama.class)
            .withSmalldClient(Config.getSmallD())
            .prefix("$$")
            .build();

    Dispatcher.init(disparse);

    Config.getSmallD().run();
  }
}
