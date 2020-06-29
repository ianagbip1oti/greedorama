package com.github.princesslana.greedorama;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;

public class Portfolio {

  private static final MonetaryAmount STARTING_CASH = Money.of(100000, "USD");

  private final ImmutableList<Transaction> transactions;

  public Portfolio() {
    this(ImmutableList.of());
  }

  public Portfolio(Iterable<Transaction> transactions) {
    this.transactions = ImmutableList.copyOf(transactions);
  }

  public MonetaryAmount getNetWorth(StockRepository stocks) {
    return getStocks(stocks).stream().map(Entry::getWorth).reduce(getCash(), MonetaryAmount::add);
  }

  public MonetaryAmount getCash() {
    return transactions
        .stream()
        .map(Transaction::getTotalPrice)
        .map(MonetaryAmount::negate)
        .reduce(STARTING_CASH, MonetaryAmount::add);
  }

  public List<Entry> getStocks(StockRepository stocks) {
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
        .collect(Collectors.toList());
  }

  private Map<String, Integer> getStockCounts() {
    return transactions
        .stream()
        .collect(
            Collectors.groupingBy(
                Transaction::getSymbol, Collectors.summingInt(Transaction::getQuantity)));
  }

  public Portfolio addTransaction(Transaction tx) {
    return new Portfolio(ImmutableList.<Transaction>builder().addAll(transactions).add(tx).build());
  }

  public static class Entry {
    private final Stock stock;
    private final int amount;

    private Entry(Stock stock, int amount) {
      this.stock = stock;
      this.amount = amount;
    }

    public String getSymbol() {
      return stock.getSymbol();
    }

    public MonetaryAmount getWorth() {
      return stock.getLatestPrice().multiply(amount);
    }
  }
}
