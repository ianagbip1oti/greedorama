package com.github.princesslana.greedorama;

import com.github.princesslana.smalld.SmallD;
import disparse.discord.smalld.Dispatcher;

public class Greedorama {

  public static void main(String[] args) {
    var smalld = SmallD.create(Config.getToken());

    var disparse =
        new Dispatcher.Builder(Greedorama.class).withSmalldClient(smalld).prefix("$$").build();

    Dispatcher.init(disparse);

    smalld.run();
  }
}
