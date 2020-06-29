package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.StockRepository;
import com.github.princesslana.greedorama.Transaction;
import com.github.princesslana.greedorama.TransactionRepository;
import com.github.princesslana.greedorama.UserRepository;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;

public class TransactionCommand {

  private final DiscordRequest request;

  private final StockRepository stocks;

  private final UserRepository users;

  private final TransactionRepository transactions;

  public TransactionCommand(
      DiscordRequest request,
      StockRepository stocks,
      UserRepository users,
      TransactionRepository transactions) {
    this.request = request;
    this.stocks = stocks;
    this.users = users;
    this.transactions = transactions;
  }

  @CommandHandler(commandName = "buy")
  public DiscordResponse buy() {
    if (request.getArgs().size() != 1) {
      return Format.error("There should be exactly one stock symbol, but there isn't");
    }

    var symbol = request.getArgs().get(0);
    var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
    var userId = request.getDispatcher().identityFromEvent(request.getEvent());
    var user = users.get(guildId, userId);

    return stocks
        .get(symbol)
        .map(
            s -> {
              var txn = Transaction.buy(user, s, 1);
              transactions.add(txn);
              return DiscordResponse.of(
                  String.format(
                      "```%s You bought 1 share of (%s) %s for %s```",
                      Emoji.BUY,
                      s.getSymbol(),
                      s.getCompanyName(),
                      Format.money(txn.getTotalPrice())));
            })
        .orElse(Format.error("Could not find price for " + symbol));
  }
}
