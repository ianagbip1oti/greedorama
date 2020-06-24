package com.github.princesslana.greedorama;

import com.fasterxml.jackson.jr.stree.JrsObject;

public class User {
  private final JrsObject json;

  private User(JrsObject json) {
    this.json = json;
  }

  public String getUsername() {
    return json.get("username").asText();
  }

  public String getDiscriminator() {
    return json.get("discriminator").asText();
  }

  public String getTag() {
    return String.format("%s#%s", getUsername(), getDiscriminator());
  }

  public static User parse(String input) {
    return new User(Json.parse(input));
  }
}
