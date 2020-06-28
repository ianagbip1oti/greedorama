package com.github.princesslana.greedorama;

import com.fasterxml.jackson.jr.stree.JrsObject;

public class User {
  private final String guildId;
  private final JrsObject json;

  private User(String guildId, JrsObject json) {
    this.guildId = guildId;
    this.json = json;
  }

  public String getId() {
    return guildId + ":" + getUserId();
  }

  private String getUserId() {
    return json.get("id").asText();
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

  public static User parse(String guildId, String input) {
    return new User(guildId, Json.parse(input));
  }
}
