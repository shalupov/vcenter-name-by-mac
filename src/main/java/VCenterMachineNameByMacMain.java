import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class VCenterMachineNameByMacMain {
  private final static Logger LOG = LoggerFactory.getLogger(VCenterMachineNameByMacMain.class);
  private final static int ListenPort = 8000;

  public static void main(String[] args) throws Exception {
    try {
      final VCenterUtils vCenterUtils = new VCenterUtils(args[0], args[1], args[2]);
      HttpServer server = HttpServer.create(new InetSocketAddress(ListenPort), 0);
      server.createContext("/bymac", new MyHandler(vCenterUtils));
      server.setExecutor(null);
      server.start();

      LOG.info("Listening on " + ListenPort);
    } catch (Throwable t) {
      LOG.error("Could not start", t);
      Runtime.getRuntime().exit(1);
    }
  }
}
