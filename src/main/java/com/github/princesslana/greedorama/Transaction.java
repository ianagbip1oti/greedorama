package com.github.princesslana.greedorama;

import java.time.Instant;
import javax.money.MonetaryAmount;

public class Transaction {

  private final Instant when;
  private final String symbol;
  private final int amount;
  private final MonetaryAmount unitPrice;

  public Transaction(Stock stock, int amount) {
    this.when = Instant.now();
    this.symbol = stock.getSymbol();
    this.amount = amount;
    this.unitPrice = stock.getLatestPrice();
  }

  public int getAmount() {
    return amount;
  }

  public String getSymbol() {
    return symbol;
  }

  public MonetaryAmount getTotalPrice() {
    return unitPrice.multiply(amount);
  }
}
