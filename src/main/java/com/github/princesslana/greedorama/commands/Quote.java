package com.github.princesslana.greedorama.commands;

import com.github.princesslana.greedorama.Emoji;
import com.github.princesslana.greedorama.StockRepository;
import com.google.common.base.Joiner;
import disparse.discord.smalld.DiscordRequest;
import disparse.discord.smalld.DiscordResponse;
import disparse.parser.reflection.CommandHandler;
import java.math.BigDecimal;

public class Quote {

  private final DiscordRequest request;

  private final StockRepository stocks;

  public Quote(DiscordRequest request, StockRepository stocks) {
    this.request = request;
    this.stocks = stocks;
  }

  @CommandHandler(commandName = "quote")
  public DiscordResponse quote() {
    if (request.getArgs().size() != 1) {
      return DiscordResponse.of(Emoji.ERROR + " You may only get a quote for one stock");
    }

    var symbol = request.getArgs().get(0);
    return stocks
        .getStock(symbol)
        .map(
            s -> {
              var info = String.format("%s %s: %s", Emoji.INFO, s.getSymbol(), s.getCompanyName());
              var price = String.format("%s $%s", Emoji.PRICE, s.getLatestPrice());

              var change = s.getChange();
              var changeEmoji =
                  change.compareTo(BigDecimal.ZERO) < 0
                      ? Emoji.DOWNWARDS_TREND
                      : Emoji.UPWARDS_TREND;

              var changeText =
                  String.format("%s $%s (%s%%)", changeEmoji, change, s.getChangePercent());

              return DiscordResponse.of(Joiner.on("\n").join(info, price, changeText));
            })
        .orElse(DiscordResponse.of(Emoji.ERROR + " Could not find quote for " + symbol));
  }
}
