import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class MyHandler implements HttpHandler {
  private final static Logger LOG = LoggerFactory.getLogger(MyHandler.class);

  private final VCenterUtils myVCenterUtils;

  public MyHandler(VCenterUtils vCenterUtils) {
    myVCenterUtils = vCenterUtils;
  }

  public void handle(HttpExchange t) throws IOException {
    final String query = t.getRequestURI().getQuery();
    if (query == null || query.trim().length() == 0) {
      LOG.warn("Empty query from {}", t.getRemoteAddress());
      sendResponse(t, 500, "Empty query");
      return;
    }

    try {
      final String name = myVCenterUtils.getMachineNameByMac(query);
      if (name == null) {
        LOG.warn("No match for query " + query);
        sendResponse(t, 404, "No match");
      } else {
        LOG.info("Query {} -> {}", query, name);
        sendResponse(t, 200, name);
      }
    } catch (Throwable ex) {
      LOG.error("Exception for query " + query, ex);
      sendResponse(t, 200, ex.getMessage());
    }
  }

  private void sendResponse(HttpExchange t, int code, String response) {
    try {
      t.sendResponseHeaders(code, response.length());

      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    } catch (Throwable ignored) {
    }
  }
}