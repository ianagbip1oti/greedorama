package com.github.princesslana.greedorama;

import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;

public class Portfolio {

  public MonetaryAmount cash = Money.of(100000, "USD");

  public MonetaryAmount getNetWorth() {
    return cash;
  }

  public MonetaryAmount getCash() {
    return cash;
  }
}
