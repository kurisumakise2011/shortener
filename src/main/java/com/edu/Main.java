package com.edu;

import com.edu.exception.RecordNotFoundException;
import com.edu.exception.UniqueViolationException;
import com.edu.exception.UnprocessableRequestException;
import com.edu.middleware.CachingMiddleware;
import com.edu.middleware.LoggingMiddleware;
import com.edu.middleware.Middleware;
import com.edu.model.Req;
import com.edu.model.Resp;
import com.edu.service.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
  public static final String DOMAIN = "https://short.en";
  private Pattern u = Pattern.compile("-u\\s+[\\S]+");
  private Pattern d = Pattern.compile("-d\\s*");
  private Pattern k = Pattern.compile("-k\\s+[\\S]+");

  public static void main(String[] args) {
    new Main().run(new Scanner(System.in));
  }

  private void run(Scanner scanner) {
    Middleware middleware = new CachingMiddleware(new LoggingMiddleware(new Service(DOMAIN)));

    System.out.println("enter -h or --help for help");
    System.out.println("enter -q to exit");
    while (true) {
      try {
        System.err.print("enter line: ");
        if (scanner.hasNext()) {
          String argLine = scanner.nextLine().trim();
          if ("-q".equals(argLine)) {
            return;
          }
          if ("-h".equals(argLine) || "--help".equals(argLine)) {
            help();
            continue;
          }
          if ("purge".equals(argLine)) {
            middleware.purge();
            System.err.println("cleaned");
            continue;
          }
          var req = parse(argLine);
          var resp = middleware.apply(req);
          print(req, resp);
        }
      } catch (Exception e) {
        handle(e);
      }
    }
  }

  private static void help() {
    System.err.println();
    System.err.println("-u <URL>            url format: -u https:/google.com/search?query=Java");
    System.err.println("-k <KEYWORD>        unique keyword: -k Programming-Blog, only [a-zA-Z0-9-_]");
    System.err.println("-d                  decode passed url");
    System.err.println("purge               clean cache and database records");
    System.err.println("-q                  quit");
    System.err.println("-h, --help          help");
  }

  private static void print(Req req, Resp resp) {
    if (req.keywordPresent()) {
      System.err.println(req.getUrl()
          + ", "
          + req.getKeyword()
          + " ===> "
          + resp.getUrl());
    } else {
      System.err.println(req.getUrl()
          + " ===> "
          + resp.getUrl());
    }
  }

  private static void handle(Exception e) {
    if (e instanceof UnprocessableRequestException) {
      System.err.println(e.getMessage());
      return;
    }
    if (e instanceof RecordNotFoundException) {
      System.err.println(e.getMessage());
      return;
    }
    if (e instanceof UniqueViolationException) {
      System.err.println(e.getMessage());
      return;
    }
    // generic exception as error
    if (e instanceof IllegalStateException) {
      System.err.println("invalid arguments: " + e.getMessage());
      return;
    }
    System.err.println("unhandled error: " + e.getMessage());
    e.printStackTrace();
  }

  private Req parse(String line) {
    if (line == null || line.isBlank()) {
      throw new UnprocessableRequestException("URL mandatory argument is absent");
    }
    var mu = this.u.matcher(line);
    if (!mu.find()) {
      throw new UnprocessableRequestException("invalid arguments");
    }
    String u = Optional.ofNullable(mu.group())
        .map(s -> s.replace("-u", ""))
        .map(String::trim).orElse(null);
    String k = null, d = null;

    var mk = this.k.matcher(line);
    if (mk.find()) {
      k = Optional.ofNullable(mk.group())
          .map(s -> s.replace("-k", ""))
          .map(String::trim).orElse(null);
    }
    var md = this.d.matcher(line);
    if (md.find()) {
      d = md.group();
    }

    if (u == null || u.isBlank()) {
      throw new UnprocessableRequestException("URL mandatory argument is absent");
    }
    URL url = getOrThrow(u);
    if (k != null && k.length() > 20) {
      throw new UnprocessableRequestException("Max length of the keyword is 20 symbols");
    }

    return new Req(url.toString(), !(d == null || d.isBlank()), k);
  }

  private static URL getOrThrow(String url) {
    try {
      return new URL(url);
    } catch (MalformedURLException e) {
      throw new UnprocessableRequestException("illegal url", e);
    }
  }
}
