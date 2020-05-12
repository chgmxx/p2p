

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyService8082 {
    public static void main(String[] args) throws Exception {
        Server jettyServer = new Server();
        SocketConnector conn = new SocketConnector();
        conn.setPort(8082);
        jettyServer.setConnectors(new Connector[]{conn});
        WebAppContext wah = new WebAppContext();
        wah.setContextPath("/task");
        wah.setWar("src/main/webapp");
        jettyServer.setHandler(wah);
        jettyServer.start();
    }
}
