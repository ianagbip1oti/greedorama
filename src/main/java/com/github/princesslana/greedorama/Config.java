package com.github.princesslana.greedorama;

import com.google.common.base.Preconditions;
import disparse.parser.reflection.Injectable;

public class Config {

  private static StockRepository STOCKS = new StockRepository();

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
}
