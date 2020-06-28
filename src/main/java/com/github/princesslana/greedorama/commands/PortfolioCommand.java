package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.StockRepository;
import com.github.princesslana.greedorama.UserRepository;
import com.google.common.base.Joiner;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;

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

  @CommandHandler(commandName = "portfolio")
  public DiscordResponse portfolio() {
    var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
    var userId = request.getDispatcher().identityFromEvent(request.getEvent());

    var user = users.get(guildId, userId);
    var portfolio = portfolios.get(user);

    var info = String.format("%s %s", Emoji.INFO, user.getTag());
    var worth = String.format("%s %s", Emoji.PRICE, Format.money(portfolio.getNetWorth(stocks)));

    var cash = String.format("%16s CASH", Format.money(portfolio.getCash()));

    var stockList = new StringBuilder();
    for (var entry : portfolio.getStocks(stocks)) {
      stockList.append(
          String.format("%16s %s%n", Format.money(entry.getWorth()), entry.getSymbol()));
    }

    return DiscordResponse.of(Joiner.on("\n").join("```", info, worth, "", cash, stockList, "```"));
  }
}
