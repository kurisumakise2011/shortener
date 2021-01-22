package com.edu.middleware;

import com.edu.model.Req;
import com.edu.model.Resp;

public interface Middleware {

  Resp apply(Req req);

  void purge();

}
