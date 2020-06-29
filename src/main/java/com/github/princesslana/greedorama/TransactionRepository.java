package com.github.princesslana.greedorama;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionRepository {

  private final List<Transaction> transactions = new ArrayList<>();

  public List<Transaction> getFor(User user) {
    return transactions
        .stream()
        .filter(txn -> txn.getUserId().equals(user.getId()))
        .collect(Collectors.toList());
  }

  public void add(Transaction txn) {
    transactions.add(txn);
  }
}
