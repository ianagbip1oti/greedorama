package com.github.princesslana.greedorama;

import com.github.princesslana.smalld.SmallD;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRepository {

  private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

  private final Cache<String, User> users =
      CacheBuilder.newBuilder().maximumSize(1500).expireAfterWrite(6, TimeUnit.HOURS).build();

  private final SmallD smalld;

  public UserRepository(SmallD smalld) {
    this.smalld = smalld;
  }

  public User get(String userId) {
    try {
      return users.get(userId, () -> this.fetch(userId));
    } catch (ExecutionException e) {
      LOG.warn("Error fetching user: {}", userId);
      throw new RuntimeException(e);
    }
  }

  private User fetch(String userId) {
    return User.parse(smalld.get("/users/" + userId));
  }
}
