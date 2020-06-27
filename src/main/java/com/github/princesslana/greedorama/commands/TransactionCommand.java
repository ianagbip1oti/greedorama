package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.StockRepository;
import com.github.princesslana.greedorama.Transaction;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;

public class TransactionCommand {

  private final DiscordRequest request;

  private final PortfolioRepository portfolios;

  private final StockRepository stocks;

  public TransactionCommand(
      DiscordRequest request, PortfolioRepository portfolios, StockRepository stocks) {
    this.request = request;
    this.portfolios = portfolios;
    this.stocks = stocks;
  }

  @CommandHandler(commandName = "buy")
  public DiscordResponse buy() {
    if (request.getArgs().size() != 1) {
      return Format.error("There should be exactly one stock symbol, but there isn't");
    }

    var symbol = request.getArgs().get(0);
    var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
    var userId = request.getDispatcher().identityFromEvent(request.getEvent());

    return stocks
        .get(symbol)
        .map(
            s -> {
              var txn = new Transaction(s, 1);
              portfolios.with(guildId, userId, p -> p.addTransaction(txn));
              return DiscordResponse.of(
                  String.format(
                      "```%s You bought 1 share of (%s) %s for %s```",
                      Emoji.BUY, symbol, s.getCompanyName(), Format.money(txn.getTotalPrice())));
            })
        .orElse(Format.error("Could not find price for " + symbol));
  }
}
