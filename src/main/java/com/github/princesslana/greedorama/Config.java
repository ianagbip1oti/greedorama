package com.github.princesslana.greedorama;

import com.github.princesslana.smalld.SmallD;
import com.google.common.base.Preconditions;
import disparse.parser.reflection.Injectable;
import java.util.Optional;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

public class Config {

  private static final SmallD SMALLD = SmallD.create(getToken());

  private static final TransactionRepository TRANSACTIONS = new TransactionRepository();
  private static final PortfolioRepository PORTFOLIOS = new PortfolioRepository(TRANSACTIONS);
  private static final StockRepository STOCKS = new StockRepository();
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

  public static DataSource getDataSource() {
    var dataDir = Optional.ofNullable(System.getenv("GOR_DATA")).orElse("data/");

    var dbName = dataDir + "greedorama.db";

    var dataSource = new EmbeddedDataSource();
    dataSource.setDatabaseName(dbName);
    dataSource.setCreateDatabase("create");
    return dataSource;
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
