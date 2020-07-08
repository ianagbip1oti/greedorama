package com.github.princesslana.greedorama.commands;

import disparse.discord.smalld.DiscordResponse;
import java.util.concurrent.Callable;

public class Try {

  private Try() {}

  public static DiscordResponse run(Callable<DiscordResponse> f) {
    try {
      return f.call();
    } catch (IllegalArgumentException e) {
      return Format.error(e.getMessage());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
