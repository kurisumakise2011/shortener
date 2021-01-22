package com.edu.middleware;

import com.edu.model.Req;
import com.edu.model.Resp;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingMiddleware implements Middleware {
  private static final Logger logger = Logger.getLogger(LoggingMiddleware.class.getName());
  private Middleware next;

  public LoggingMiddleware(Middleware next) {
    Objects.requireNonNull(next);
    this.next = next;
  }

  @Override
  public Resp apply(Req req) {
    logger.log(Level.INFO, req.toString());
    var resp =  next.apply(req);
    logger.log(Level.INFO, resp.toString());
    return resp;
  }

  @Override
  public void purge() {
    next.purge();
  }
}
