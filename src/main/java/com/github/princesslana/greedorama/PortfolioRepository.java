package com.github.princesslana.greedorama;

public class PortfolioRepository {

  private final StockRepository stocks;

  private final TransactionRepository transactions;

  public PortfolioRepository(StockRepository stocks, TransactionRepository transactions) {
    this.stocks = stocks;
    this.transactions = transactions;
  }

  public Portfolio get(User user) {
    return new Portfolio(stocks, transactions.getFor(user));
  }
}
