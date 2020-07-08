package com.github.princesslana.greedorama.commands;

import disparse.discord.smalld.DiscordResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryFormats;
import org.javamoney.moneta.format.CurrencyStyle;

public class Format {
  private Format() {}

  public static String money(MonetaryAmount amt) {
    var fmt =
        MonetaryFormats.getAmountFormat(
            AmountFormatQueryBuilder.of(Locale.US).set(CurrencyStyle.SYMBOL).build());

    return fmt.format(amt);
  }

  public static String date(Instant inst) {
    var fmt =
        DateTimeFormatter.ofPattern("MMM dd uuuu")
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    return fmt.format(inst);
  }

  public static String time(Instant inst) {
    var fmt =
        DateTimeFormatter.ofPattern("hh:mm a")
            .withLocale(Locale.getDefault())
            .withZone(ZoneId.systemDefault());

    return fmt.format(inst);
  }

  public static DiscordResponse error(String msg) {
    return DiscordResponse.of(String.format("```%s %s```", Emoji.ERROR, msg));
  }
}
