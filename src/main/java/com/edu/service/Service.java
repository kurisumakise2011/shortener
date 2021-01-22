package com.edu.service;

import com.edu.exception.UnprocessableRequestException;
import com.edu.middleware.Middleware;
import com.edu.model.Record;
import com.edu.model.Req;
import com.edu.model.Resp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Service implements Middleware {
  private final String domain;

  private static final int BASE = 62;
  private static Map<Long, Character> basement = new HashMap<>();
  private static Map<Character, Long> reverse = new HashMap<>();
  private final Table table = new Table();

  static {
    long i = 0;
    for (char c = 'a'; c <= 'z'; c++, i++) {
      basement.put(i, c);
      reverse.put(c, i);
    }
    for (char c = 'A'; c <= 'Z'; c++, i++) {
      basement.put(i, c);
      reverse.put(c, i);
    }
    for (char c = '0'; c <= '9'; c++, i++) {
      basement.put(i, c);
      reverse.put(c, i);
    }
  }

  public Service(String domain) {
    this.domain = domain;
  }

  @Override
  public Resp apply(Req req) {
    return req.isDecode() ? decode(req) : encode(req);
  }

  @Override
  public void purge() {
    table.purge();
  }

  private Resp decode(Req req) {
    String url = req.getUrl();
    int slash = url.lastIndexOf('/');
    if (slash == -1) {
      throw new UnprocessableRequestException("invalid url: " + url);
    }

    String encoded = url.substring(slash + 1);

    Record byKeyword = table.getByKeyword(encoded);
    if (byKeyword != null) {
      return new Resp(byKeyword.getUrl());
    }

    long id = decode(encoded);
    Record record = table.getById(id);
    return new Resp(record.getUrl());
  }

  private long decode(String encoded) {
    char[] chars = encoded.toCharArray();
    long id = 0;
    for (int i = 0; i < chars.length; i++) {
      Long r = reverse.get(chars[i]);
      if (r == null) {
        throw new UnprocessableRequestException("malformed uri: " + encoded);
      }
      id += r * Math.pow(BASE, i);
    }
    return id;
  }

  private Resp encode(Req req) {
    long id = table.genId();
    String uri;
    if (req.keywordPresent()) {
      uri = req.getKeyword();
    } else {
      uri = encode(id);
    }
    Record record = new Record();
    record.setId(id);
    record.setKeyword(req.getKeyword());
    record.setUrl(req.getUrl());
    table.save(record);

    return new Resp(domain + "/" + uri);
  }

  private String encode(long id) {
    LinkedList<Long> digits = new LinkedList<>();
    while (id > 0) {
      long r = id % BASE;
      digits.push(r);
      id /= BASE;
    }
    var builder = new StringBuilder();
    while (!digits.isEmpty()) {
      builder.append(basement.get(digits.pollLast()));
    }
    return builder.toString();
  }
}
