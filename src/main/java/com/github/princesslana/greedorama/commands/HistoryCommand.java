package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.Transaction;
import com.github.princesslana.greedorama.UserRepository;
import com.google.common.base.Joiner;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import java.util.HashSet;

public class HistoryCommand {

  private static final int TXNS_PER_PAGE = 10;

  private final DiscordRequest request;

  private final PortfolioRepository portfolios;

  private final UserRepository users;

  public HistoryCommand(
      DiscordRequest request, PortfolioRepository portfolios, UserRepository users) {
    this.request = request;
    this.portfolios = portfolios;
    this.users = users;
  }

  @ParsedEntity
  private static class Options {
    @Flag(shortName = 'p', longName = "page", description = "Display page")
    public Integer page = 1;

    @Flag(shortName = 'v', longName = "verbose", description = "Display more detail")
    public boolean verbose = false;
  }

  @CommandHandler(commandName = "history")
  public DiscordResponse portfolio(Options options) {
    var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
    var userId = request.getDispatcher().identityFromEvent(request.getEvent());

    var user = users.get(guildId, userId);
    var portfolio = portfolios.get(user);

    var txns = Transaction.BY_WHEN.sortedCopy(portfolio.getTransactions());
    var totalPages = (txns.size() + TXNS_PER_PAGE - 1) / TXNS_PER_PAGE;
    var page = options.page;

    var pageText = String.format("Page %d of %d", options.page, totalPages);

    if (options.page > totalPages || options.page <= 0) {
      return Format.error("Page number out of range");
    }

    var dates = new HashSet<String>();
    var txnList = new StringBuilder();

    for (var i = (page - 1) * TXNS_PER_PAGE; i < page * TXNS_PER_PAGE; i++) {

      if (i >= txns.size()) {
        break;
      }

      var txn = txns.get(i);
      var date = Format.date(txn.getWhen());

      var line = new String();

      if (options.verbose) {
        line +=
            String.format(
                "%15s %s %s %d shares of %s for %s @ %s per share",
                dates.contains(date) ? "" : date + ":",
                (txn.getQuantity() > 0) ? Emoji.BUY : Emoji.SELL,
                (txn.getQuantity() > 0) ? "Bought" : "Sold",
                Math.abs(txn.getQuantity()),
                txn.getSymbol(),
                Format.money(txn.getTotalPrice().abs()),
                Format.money(txn.getUnitPrice()));

        dates.add(date);
      } else {
        line +=
            String.format(
                "%15s %11s %s",
                Format.shortDate(txn.getWhen()),
                Format.money(txn.getTotalPrice()).substring(1),
                txn.getSymbol());
      }

      txnList.append(line + "\n");
    }

    return DiscordResponse.of(
        Joiner.on("\n").join("```", "Transactions", txnList.toString(), pageText, "```"));
  }
}
