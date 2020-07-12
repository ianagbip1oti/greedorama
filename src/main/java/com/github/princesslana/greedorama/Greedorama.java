package com.github.princesslana.greedorama;

import com.github.princesslana.somedb.EnableJooqCodeGen;
import com.github.princesslana.somedb.TheDB;
import disparse.discord.smalld.Dispatcher;

@EnableJooqCodeGen(packageName = "com.github.princesslana.greedorama.db")
public class Greedorama {

  public static void main(String[] args) {
    TheDB.initialize("greedorama");

    var disparse =
        new Dispatcher.Builder(Greedorama.class)
            .withSmalldClient(Config.getSmallD())
            .prefix("$$")
            .build();

    Dispatcher.init(disparse);

    Config.getSmallD().run();
  }
}
