package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.Portfolio;
import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.StockRepository;
import com.github.princesslana.greedorama.UserRepository;
import com.google.common.base.Ascii;
import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

public class PortfolioCommand {

  private final DiscordRequest request;

  private final PortfolioRepository portfolios;

  private final UserRepository users;

  private final StockRepository stocks;

  public PortfolioCommand(
      DiscordRequest request,
      PortfolioRepository portfolios,
      UserRepository users,
      StockRepository stocks) {
    this.request = request;
    this.portfolios = portfolios;
    this.users = users;
    this.stocks = stocks;
  }

  @ParsedEntity
  private static class Options {
    @Flag(shortName = 'v', longName = "verbose", description = "Display more detail")
    public boolean verbose = false;
  }

  @CommandHandler(commandName = "portfolio")
  public DiscordResponse portfolio(Options options) {
    var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
    var userId = request.getDispatcher().identityFromEvent(request.getEvent());

    var user = users.get(guildId, userId);
    var portfolio = portfolios.get(user);

    var info = String.format("%s %s", Emoji.INFO, user.getTag());
    var worth = String.format("%s %s", Emoji.PRICE, Format.money(portfolio.getNetWorth(stocks)));

    var cash = String.format("%16s CASH", Format.money(portfolio.getCash()));

    var stockList = new StringBuilder();

    var byNetWorth = Ordering.natural().reverse().onResultOf(Portfolio.Entry::getWorth);

    for (var entry : byNetWorth.sortedCopy(portfolio.getStocks(stocks))) {
      var line = String.format("%16s %-5s", Format.money(entry.getWorth()), entry.getSymbol());

      if (options.verbose) {
        line +=
            String.format(
                " %-20s %6d @ %10s (%7s)",
                Ascii.truncate(entry.getCompanyName(), 20, "..."),
                entry.getQuantity(),
                Format.money(entry.getLatestPrice()),
                Format.money(entry.getChange()));
      }

      stockList.append(line + "\n");
    }

    return DiscordResponse.of(Joiner.on("\n").join("```", info, worth, "", cash, stockList, "```"));
  }
}
