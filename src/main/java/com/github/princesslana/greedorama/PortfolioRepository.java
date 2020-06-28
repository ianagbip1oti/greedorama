package com.github.princesslana.greedorama;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class PortfolioRepository {

  private Map<String, Portfolio> store = new HashMap<>();

  public Portfolio get(User user) {
    return Optional.ofNullable(store.get(user.getId())).orElse(new Portfolio());
  }

  public void with(User user, UnaryOperator<Portfolio> op) {
    store.put(user.getId(), op.apply(get(user)));
  }
}
