package com.edu.model;

public class Resp {
  private final String url;

  public Resp(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return "Resp{" +
        "url='" + url + '\'' +
        '}';
  }
}
