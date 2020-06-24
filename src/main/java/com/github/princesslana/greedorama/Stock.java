package com.github.princesslana.greedorama;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.stree.JacksonJrsTreeCodec;
import com.fasterxml.jackson.jr.stree.JrsObject;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

  public BigDecimal getLatestPrice() {
    return getDecimal("latestPrice");
  }

  public BigDecimal getChange() {
    return getDecimal("change");
  }

  public BigDecimal getChangePercent() {
    return getDecimal("changePercent");
  }

  static Stock parse(String input) throws IOException {
    var parser = JSON.builder().treeCodec(new JacksonJrsTreeCodec()).build();

    var json = (JrsObject) parser.treeFrom(input);

    return new Stock(json);
  }

  private BigDecimal getDecimal(String key) {
    return new BigDecimal(json.get(key).asText()).setScale(2, RoundingMode.HALF_UP);
  }
}
