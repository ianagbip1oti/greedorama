package com.github.princesslana.greedorama;

import com.github.princesslana.smalld.SmallD;
import com.google.common.base.Preconditions;
import disparse.parser.reflection.Injectable;
import java.util.Optional;
import javax.sql.DataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

public class Config {

  private static final StockRepository STOCKS = new StockRepository();

  private static final SmallD SMALLD = Smalld.create(getToken());

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
}
