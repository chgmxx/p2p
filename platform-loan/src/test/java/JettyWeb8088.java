

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyWeb8088 {
    public static void main(String[] args) throws Exception {
        Server jettyServer = new Server();
        SocketConnector conn = new SocketConnector();
        conn.setPort(8088);
        jettyServer.setConnectors(new Connector[]{conn});
        WebAppContext wah = new WebAppContext();
        wah.setContextPath("/web");
        wah.setWar("src/main/webapp");
        jettyServer.setHandler(wah);
        jettyServer.start();
    }
}
