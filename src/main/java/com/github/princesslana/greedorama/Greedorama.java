package com.github.princesslana.greedorama;

import disparse.discord.smalld.Dispatcher;
import org.flywaydb.core.Flyway;

public class Greedorama {

  public static void main(String[] args) {
    Flyway.configure().dataSource(Config.getDataSource()).load().migrate();

    var disparse =
        new Dispatcher.Builder(Greedorama.class)
            .withSmalldClient(Config.getSmallD())
            .prefix("$$")
            .build();

    Dispatcher.init(disparse);

    Config.getSmallD().run();
  }
}
