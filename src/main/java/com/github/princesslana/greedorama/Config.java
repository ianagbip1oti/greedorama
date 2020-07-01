package com.github.princesslana.greedorama;

import com.github.princesslana.smalld.SmallD;
import com.google.common.base.Preconditions;
import disparse.parser.reflection.Injectable;

public class Config {

  private static final SmallD SMALLD = SmallD.create(getToken());

  private static final StockRepository STOCKS = new StockRepository();
  private static final TransactionRepository TRANSACTIONS = new TransactionRepository();
  private static final PortfolioRepository PORTFOLIOS =
      new PortfolioRepository(STOCKS, TRANSACTIONS);
  private static final UserRepository USERS = new UserRepository(SMALLD);

  public static SmallD getSmallD() {
    return SMALLD;
  }

  public static String getToken() {
    return Preconditions.checkNotNull(System.getenv("GOR_TOKEN"));
  }

  public static String getIexPublicKey() {
    return Preconditions.checkNotNull(System.getenv("GOR_IEX_PUBLIC"));
  }

  @Injectable
  public static StockRepository getStockRepository() {
    return STOCKS;
  }

  @Injectable
  public static PortfolioRepository getPortfolioRepository() {
    return PORTFOLIOS;
  }

  @Injectable
  public static TransactionRepository getTransactionRepository() {
    return TRANSACTIONS;
  }

  @Injectable
  public static UserRepository getUserRepository() {
    return USERS;
  }
}
