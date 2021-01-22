package com.edu.model;

import java.util.Objects;

public class Req {
  private final String url;
  private final String keyword;
  private final boolean decode;

  public Req(String url, boolean decode, String keyword) {
    this.url = url;
    this.decode = decode;
    this.keyword = keyword;
  }

  public String getUrl() {
    return url;
  }

  public boolean isDecode() {
    return decode;
  }

  public String getKeyword() {
    return keyword;
  }

  public boolean keywordPresent() {
    return !(keyword == null || keyword.isBlank());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Req req = (Req) o;
    return decode == req.decode &&
        Objects.equals(url, req.url) &&
        Objects.equals(keyword, req.keyword);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, keyword, decode);
  }

  @Override
  public String toString() {
    return "Req{" +
        "url=" + url +
        ", keyword='" + keyword + '\'' +
        ", decode=" + decode +
        '}';
  }
}
