package com.github.princesslana.greedorama;

import java.time.Instant;
import javax.money.MonetaryAmount;

public class Transaction {

  private final String userId;
  private final Instant when;
  private final String symbol;
  private final int amount;
  private final MonetaryAmount unitPrice;

  public Transaction(
      String userId, Instant when, String symbol, int amount, MonetaryAmount unitPrice) {
    this.userId = userId;
    this.when = when;
    this.symbol = symbol;
    this.amount = amount;
    this.unitPrice = unitPrice;
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

  public static Transaction buy(User who, Stock stock, int amount) {
    return new Transaction(
        who.getId(), Instant.now(), stock.getSymbol(), amount, stock.getLatestPrice());
  }
}
