package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.StockRepository;
import com.github.princesslana.greedorama.Transaction;
import com.github.princesslana.greedorama.TransactionRepository;
import com.github.princesslana.greedorama.UserRepository;
import com.google.common.base.Preconditions;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

public class TransactionCommand {

  private final DiscordRequest request;

  private final StockRepository stocks;

  private final UserRepository users;

  private final PortfolioRepository portfolios;

  private final TransactionRepository transactions;

  public TransactionCommand(
      DiscordRequest request,
      StockRepository stocks,
      UserRepository users,
      PortfolioRepository portfolios,
      TransactionRepository transactions) {
    this.request = request;
    this.stocks = stocks;
    this.users = users;
    this.portfolios = portfolios;
    this.transactions = transactions;
  }

  @ParsedEntity
  private static class Options {
    @Flag(shortName = 'n', longName = "number", description = "Number of shares (default: 1)")
    public Integer number = 1;
  }

  private DiscordResponse createTransaction(Options options, int quantity) {
    Preconditions.checkArgument(options.number > 0, "Number of shares must be greater than zero");
    Preconditions.checkArgument(
        request.getArgs().size() == 1, "There should be exactly one stock symbol");

    var symbol = request.getArgs().get(0);
    var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
    var userId = request.getDispatcher().identityFromEvent(request.getEvent());
    var user = users.get(guildId, userId);
    var portfolio = portfolios.get(user);

    var stock =
        stocks
            .get(symbol)
            .orElseThrow(() -> new IllegalArgumentException("Could not find price for " + symbol));
    var txn = Transaction.create(user, stock, quantity);

    portfolio.check(txn);

    transactions.add(txn);

    return DiscordResponse.of(
        String.format(
            "```%s You %s %d share(s) of (%s) %s for %s```",
            (quantity > 0) ? Emoji.BUY : Emoji.SELL,
            (quantity > 0) ? "bought" : "sold",
            options.number,
            stock.getSymbol(),
            stock.getCompanyName(),
            Format.money(txn.getTotalPrice().abs())));
  }

  @CommandHandler(commandName = "buy")
  public DiscordResponse buy(Options options) {
    return Try.run(() -> createTransaction(options, options.number));
  }

  @CommandHandler(commandName = "sell")
  public DiscordResponse sell(Options options) {
    return Try.run(() -> createTransaction(options, -options.number));
  }
}
