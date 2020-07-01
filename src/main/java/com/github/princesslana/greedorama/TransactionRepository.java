package com.github.princesslana.greedorama;

import com.github.princesslana.somedb.TheDB;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;

public class TransactionRepository {

  private final List<Transaction> transactions = new ArrayList<>();

  public List<Transaction> getFor(User user) {
    return TheDB.select(
            TransactionRepository::fromResultSet,
            "select user_id, created_at, symbol, quantity, unit_price "
                + "from transactions where user_id = ?",
            user.getId())
        .collect(Collectors.toList());
  }

  public void add(Transaction txn) {
    TheDB.execute(
        "insert into transactions(user_id, created_at, symbol, quantity, unit_price) "
            + "values(?, ?, ?, ?, ?)",
        txn.getUserId(),
        txn.getWhen(),
        txn.getSymbol(),
        txn.getQuantity(),
        txn.getUnitPrice().getNumber().numberValueExact(BigDecimal.class));
  }

  public static Transaction fromResultSet(ResultSet rs) throws SQLException {
    return new Transaction(
        rs.getString("user_id"),
        rs.getTimestamp("created_at").toInstant(),
        rs.getString("symbol"),
        rs.getInt("quantity"),
        Money.of(rs.getBigDecimal("unit_price"), "USD"));
  }
}
