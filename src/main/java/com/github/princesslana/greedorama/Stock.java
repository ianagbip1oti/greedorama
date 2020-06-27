package com.github.princesslana.greedorama;

import com.fasterxml.jackson.jr.stree.JrsObject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;

public class Stock {

  private final JrsObject json;

  private Stock(JrsObject json) {
    this.json = json;
  }

  public String getCompanyName() {
    return json.get("companyName").asText();
  }

  public String getSymbol() {
    return json.get("symbol").asText();
  }

  public MonetaryAmount getLatestPrice() {
    return getMoney("latestPrice");
  }

  public MonetaryAmount getChange() {
    return getMoney("change");
  }

  public BigDecimal getChangePercent() {
    return getDecimal("changePercent");
  }

  static Stock parse(String input) {
    return new Stock(Json.parse(input));
  }

  private MonetaryAmount getMoney(String key) {
    return Money.of(getDecimal(key), "USD");
  }

  private BigDecimal getDecimal(String key) {
    return new BigDecimal(json.get(key).asText()).setScale(2, RoundingMode.HALF_UP);
  }
}
