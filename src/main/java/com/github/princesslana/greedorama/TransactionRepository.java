package com.github.princesslana.greedorama;

import static com.github.princesslana.greedorama.db.Tables.TRANSACTIONS;

import com.github.princesslana.somedb.TheDB;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.javamoney.moneta.Money;

public class TransactionRepository {

  private final List<Transaction> transactions = new ArrayList<>();

  public List<Transaction> getFor(User user) {
    return TheDB.jooq()
        .select(
            TRANSACTIONS.USER_ID,
            TRANSACTIONS.CREATED_AT,
            TRANSACTIONS.SYMBOL,
            TRANSACTIONS.QUANTITY,
            TRANSACTIONS.UNIT_PRICE)
        .from(TRANSACTIONS)
        .where(TRANSACTIONS.USER_ID.eq(user.getId()))
        .fetch(
            row ->
                new Transaction(
                    row.getValue(TRANSACTIONS.USER_ID),
                    row.getValue(TRANSACTIONS.CREATED_AT)
                        .atZone(ZoneOffset.systemDefault())
                        .toInstant(),
                    row.getValue(TRANSACTIONS.SYMBOL),
                    row.getValue(TRANSACTIONS.QUANTITY),
                    Money.of(row.getValue(TRANSACTIONS.UNIT_PRICE), "USD")));
  }

  public void add(Transaction txn) {
    TheDB.jooq()
        .insertInto(
            TRANSACTIONS,
            TRANSACTIONS.USER_ID,
            TRANSACTIONS.CREATED_AT,
            TRANSACTIONS.SYMBOL,
            TRANSACTIONS.QUANTITY,
            TRANSACTIONS.UNIT_PRICE)
        .values(
            txn.getUserId(),
            LocalDateTime.ofInstant(txn.getWhen(), ZoneOffset.systemDefault()),
            txn.getSymbol(),
            txn.getQuantity(),
            txn.getUnitPrice().getNumber().numberValueExact(BigDecimal.class))
        .execute();
  }
}
