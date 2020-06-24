package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.PortfolioRepository;
import com.github.princesslana.greedorama.UserRepository;
import com.google.common.base.Joiner;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;

public class PortfolioCommand {

  private final DiscordRequest request;

  private final PortfolioRepository portfolios;

  private final UserRepository users;

  public PortfolioCommand(
      DiscordRequest request, PortfolioRepository portfolios, UserRepository users) {
    this.request = request;
    this.portfolios = portfolios;
    this.users = users;
  }

  @CommandHandler(commandName = "portfolio")
  public DiscordResponse portfolio() {
    var guildId = request.getDispatcher().guildFromEvent(request.getEvent());
    var userId = request.getDispatcher().identityFromEvent(request.getEvent());

    var portfolio = portfolios.get(guildId, userId);
    var user = users.get(userId);

    var info = String.format("%s %s", Emoji.INFO, user.getTag());
    var worth = String.format("%s %s", Emoji.PRICE, Format.money(portfolio.getNetWorth()));

    var cash = String.format("%16s CASH", Format.money(portfolio.getCash()));

    return DiscordResponse.of(Joiner.on("\n").join("```", info, worth, "", cash, "```"));
  }
}
