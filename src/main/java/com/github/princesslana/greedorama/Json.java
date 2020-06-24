package com.github.princesslana.greedorama;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.stree.JacksonJrsTreeCodec;
import com.fasterxml.jackson.jr.stree.JrsObject;
import java.io.IOException;

public class Json {
  private static final JSON PARSER = JSON.builder().treeCodec(new JacksonJrsTreeCodec()).build();

  private Json() {}

  public static JrsObject parse(String input) {
    try {
      return (JrsObject) PARSER.treeFrom(input);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
