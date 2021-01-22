package com.edu.middleware;

import com.edu.model.Req;
import com.edu.model.Resp;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CachingMiddleware implements Middleware {
  private final Map<Req, Resp> cache = new ConcurrentHashMap<>();

  private Middleware next;

  public CachingMiddleware(Middleware next) {
    Objects.requireNonNull(next);
    this.next = next;
  }

  @Override
  public Resp apply(Req req) {
    var cached = cache.get(req);
    if (cached == null) {
      var resp = next.apply(req);
      cache.put(req, resp);
      return resp;
    }
    return cached;
  }

  @Override
  public void purge() {
    cache.clear();
    next.purge();
  }
}
