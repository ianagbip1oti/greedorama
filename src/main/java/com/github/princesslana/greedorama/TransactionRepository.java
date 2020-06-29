package com.github.princesslana.greedorama;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.javamoney.moneta.Money;
import org.jdbi.v3.core.Jdbi;

public class TransactionRepository {

  private final Jdbi jdbi;

  private final List<Transaction> transactions = new ArrayList<>();

  public TransactionRepository(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  public List<Transaction> getFor(User user) {
    return jdbi.withHandle(
        h ->
            h.createQuery(
                    "select user_id, created_at, symbol, quantity, unit_price "
                        + " from transactions where user_id = :user_id")
                .bind("user_id", user.getId())
                .map(
                    (rs, ctx) ->
                        new Transaction(
                            rs.getString("user_id"),
                            rs.getTimestamp("created_at").toInstant(),
                            rs.getString("symbol"),
                            rs.getInt("quantity"),
                            Money.of(rs.getBigDecimal("unit_price"), "USD")))
                .list());
  }

  public void add(Transaction txn) {
    jdbi.useHandle(
        h ->
            h.createUpdate(
                    "insert into transactions "
                        + "(user_id, created_at, symbol, quantity, unit_price) "
                        + " values(:user_id, :created_at, :symbol, :quantity, :unit_price)")
                .bind("user_id", txn.getUserId())
                .bind("created_at", txn.getWhen())
                .bind("symbol", txn.getSymbol())
                .bind("quantity", txn.getQuantity())
                .bind(
                    "unit_price", txn.getUnitPrice().getNumber().numberValueExact(BigDecimal.class))
                .execute());
  }
}
