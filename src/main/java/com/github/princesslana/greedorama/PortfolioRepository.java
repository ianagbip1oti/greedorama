package com.github.princesslana.greedorama;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class PortfolioRepository {

  // Currently the key is guildId + userId
  // This isn't great, but is only until we use the db instead of in memory
  private Map<String, Portfolio> store = new HashMap<>();

  public Portfolio get(String guildId, String userId) {
    return Optional.ofNullable(store.get(guildId + userId)).orElse(new Portfolio());
  }

  public void with(String guildId, String userId, UnaryOperator<Portfolio> op) {
    store.put(guildId + userId, op.apply(get(guildId, userId)));
  }
}
