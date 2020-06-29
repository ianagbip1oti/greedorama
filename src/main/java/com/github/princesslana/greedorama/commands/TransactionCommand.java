package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.StockRepository;
import com.github.princesslana.greedorama.Transaction;
import com.github.princesslana.greedorama.UserRepository;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;

public class TransactionCommand {

  private final DiscordRequest request;

  private final PortfolioRepository portfolios;

  private final StockRepository stocks;

  private final UserRepository users;

  public TransactionCommand(
      DiscordRequest request,
      PortfolioRepository portfolios,
      StockRepository stocks,
      UserRepository users) {
    this.request = request;
    this.portfolios = portfolios;
    this.stocks = stocks;
    this.users = users;
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
              portfolios.with(user, p -> p.addTransaction(txn));
              return DiscordResponse.of(
                  String.format(
                      "```%s You bought 1 share of (%s) %s for %s```",
                      Emoji.BUY, symbol, s.getCompanyName(), Format.money(txn.getTotalPrice())));
            })
        .orElse(Format.error("Could not find price for " + symbol));
  }
}
