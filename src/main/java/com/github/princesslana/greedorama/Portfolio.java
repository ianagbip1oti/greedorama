package com.github.princesslana.greedorama;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;

public class Portfolio {

  private static final MonetaryAmount STARTING_CASH = Money.of(100000, "USD");

  public static final Ordering<Entry> BY_NET_WORTH =
          Ordering.natural().reverse().onResultOf(Entry::getWorth);

  private final StockRepository stocks;

  private final ImmutableList<Transaction> transactions;

  public Portfolio(StockRepository stocks) {
    this(stocks, ImmutableList.of());
  }

  public Portfolio(StockRepository stocks, Iterable<Transaction> transactions) {
    this.stocks = stocks;
    this.transactions = ImmutableList.copyOf(transactions);
  }

  public void check(Transaction txn) {
    Preconditions.checkArgument(
        getCash().isGreaterThanOrEqualTo(txn.getTotalPrice()), "Insufficient funds");

    var currentCount = getStockCounts().getOrDefault(txn.getSymbol(), 0);
    Preconditions.checkArgument(
        currentCount + txn.getQuantity() >= 0, "You do not have that amount of shares");
  }

  public MonetaryAmount getNetWorth() {
    return getStocks().stream().map(Entry::getWorth).reduce(getCash(), MonetaryAmount::add);
  }

  public MonetaryAmount getNetWorthChange() {
    return getStocks()
        .stream()
        .map(s -> s.getChange().multiply(s.getQuantity()))
        .reduce(Money.of(0, "USD"), MonetaryAmount::add);
  }

  public BigDecimal getNetWorthChangePercent() {
    var now = getNetWorth();
    var change = getNetWorthChange();
    var old = now.subtract(change);

    var changeD = change.getNumber().numberValueExact(Double.class);
    var oldD = old.getNumber().numberValueExact(Double.class);

    var changePct = changeD / oldD * 100.0;

    return BigDecimal.valueOf(changePct).setScale(2, RoundingMode.HALF_UP);
  }

  public MonetaryAmount getCash() {
    return transactions
        .stream()
        .map(Transaction::getTotalPrice)
        .map(MonetaryAmount::negate)
        .reduce(STARTING_CASH, MonetaryAmount::add);
  }

  public List<Entry> getStocks() {
    return getStockCounts()
        .entrySet()
        .stream()
        .map(
            e ->
                new Entry(
                    stocks
                        .get(e.getKey())
                        .orElseThrow(
                            () ->
                                new IllegalStateException(
                                    "Unknown stock in portfolio: " + e.getKey())),
                    e.getValue()))
        .filter(e -> e.getQuantity() != 0)
        .collect(Collectors.toList());
  }

  public ImmutableList<Transaction> getTransactions() {
    return transactions;
  }

  private Map<String, Integer> getStockCounts() {
    return transactions
        .stream()
        .collect(
            Collectors.groupingBy(
                Transaction::getSymbol, Collectors.summingInt(Transaction::getQuantity)));
  }

  public static class Entry {
    private final Stock stock;
    private final int quantity;

    private Entry(Stock stock, int quantity) {
      this.stock = stock;
      this.quantity = quantity;
    }

    public String getSymbol() {
      return stock.getSymbol();
    }

    public String getCompanyName() {
      return stock.getCompanyName();
    }

    public MonetaryAmount getWorth() {
      return stock.getLatestPrice().multiply(quantity);
    }

    public MonetaryAmount getLatestPrice() {
      return stock.getLatestPrice();
    }

    public MonetaryAmount getChange() {
      return stock.getChange();
    }

    public int getQuantity() {
      return quantity;
    }
  }
}
