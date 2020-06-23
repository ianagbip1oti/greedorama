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
    return new BigDecimal(json.get("latestPrice").asText());
  }

  public BigDecimal getChange() {
    return new BigDecimal(json.get("change").asText());
  }

  public BigDecimal getChangePercent() {
    return new BigDecimal(json.get("changePercent").asText()).setScale(2, RoundingMode.HALF_UP);
  }

  static Stock parse(String input) throws IOException {
    var parser = JSON.builder().treeCodec(new JacksonJrsTreeCodec()).build();

    var json = (JrsObject) parser.treeFrom(input);

    return new Stock(json);
  }
}
