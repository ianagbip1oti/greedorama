package com.github.princesslana.greedorama;

import com.google.common.collect.Ordering;
import java.time.Instant;
import javax.money.MonetaryAmount;

public class Transaction {

  public static final Ordering<Transaction> BY_WHEN =
          Ordering.natural().reverse().onResultOf(Transaction::getWhen);

  private final String userId;
  private final Instant when;
  private final String symbol;
  private final int quantity;
  private final MonetaryAmount unitPrice;

  public Transaction(
      String userId, Instant when, String symbol, int quantity, MonetaryAmount unitPrice) {
    this.userId = userId;
    this.when = when;
    this.symbol = symbol;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  public String getUserId() {
    return userId;
  }

  public Instant getWhen() {
    return when;
  }

  public int getQuantity() {
    return quantity;
  }

  public String getSymbol() {
    return symbol;
  }

  public MonetaryAmount getUnitPrice() {
    return unitPrice;
  }

  public MonetaryAmount getTotalPrice() {
    return unitPrice.multiply(quantity);
  }

  public static Transaction create(User who, Stock stock, int quantity) {
    return new Transaction(
        who.getId(), Instant.now(), stock.getSymbol(), quantity, stock.getLatestPrice());
  }
}
