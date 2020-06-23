package com.github.princesslana.greedorama;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockRepository {

  private static final Logger LOG = LoggerFactory.getLogger(StockRepository.class);

  private final HttpClient http = HttpClient.newHttpClient();

  private Cache<String, Optional<Stock>> stocks =
      CacheBuilder.newBuilder().maximumSize(1500).expireAfterWrite(15, TimeUnit.MINUTES).build();

  public Optional<Stock> getStock(String symbol) {
    try {
      return stocks.get(
          symbol,
          () -> {
            var request =
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "https://cloud.iexapis.com/stable/stock/"
                                + symbol
                                + "/quote?displayPercent=true&token="
                                + Config.getIexPublicKey()))
                    .build();

            try {
              var response = http.send(request, HttpResponse.BodyHandlers.ofString());

              return response.statusCode() == 200
                  ? Optional.of(Stock.parse(response.body()))
                  : Optional.empty();
            } catch (IOException e) {
              LOG.warn("Error fetching stock: {}", symbol, e);
              return Optional.empty();
            }
          });
    } catch (ExecutionException e) {
      LOG.warn("Error fetching stock: {}", symbol, e);
      return Optional.empty();
    }
  }
}
