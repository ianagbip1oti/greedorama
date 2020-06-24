package com.github.princesslana.greedorama.commands;

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
}
