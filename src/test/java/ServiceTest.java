import com.edu.Main;
import com.edu.model.Req;
import com.edu.model.Resp;
import com.edu.service.Service;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceTest {
  private Service service = new Service(Main.DOMAIN);

  @Test
  public void encodeAndDecode() {
    Req reqe = new Req("https://blog.mysite.com/another-article", false, "");
    Resp rese = service.apply(reqe);
    assertTrue(rese.getUrl().contains(Main.DOMAIN));
    System.out.println(rese.getUrl());
    Req reqd = new Req(rese.getUrl(), true, "");
    Resp resd = service.apply(reqd);
    assertEquals(reqe.getUrl(), resd.getUrl());
  }

  @Test
  public void encodeAndDecodeKeyword() {
    Req reqe = new Req("https://blog.mysite.com/another-article", false, "BEST-ARTICLE");
    Resp rese = service.apply(reqe);
    assertEquals("https://short.en/BEST-ARTICLE", rese.getUrl());
    System.out.println(rese.getUrl());
    Req reqd = new Req(rese.getUrl(), true, "");
    Resp resd = service.apply(reqd);
    assertEquals(reqe.getUrl(), resd.getUrl());
  }

}
