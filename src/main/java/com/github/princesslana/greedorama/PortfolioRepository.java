package com.github.princesslana.greedorama;

public class PortfolioRepository {

  private final TransactionRepository transactions;

  public PortfolioRepository(TransactionRepository transactions) {
    this.transactions = transactions;
  }

  public Portfolio get(User user) {
    return new Portfolio(transactions.getFor(user));
  }
}
