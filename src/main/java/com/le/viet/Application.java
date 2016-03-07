package com.le.viet;

import com.le.viet.servlet.ConnectWSServlet;
import com.le.viet.servlet.SSLServlet;
import com.le.viet.ws.HelloWebSocket;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.security.ProtectionDomain;

@SpringBootApplication
public class Application{
    public static void main(String[] args) {
        //runServer();
        setupSSL();
    }

    private static void runServer(){
        try {
            //SpringApplication.run(Application.class, args);
            System.out.println("Hi");
            Server server = new Server(3002);
            ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            handler.setContextPath("/poc/ws");
            server.setHandler(handler);
            handler.addServlet(new ServletHolder(new SSLServlet()), "/hello");
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setupSSL(){
        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(3000);

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(Application.class.getResource("/keystore.jks").toExternalForm());

        sslContextFactory.setKeyStorePassword("123456");
        sslContextFactory.setKeyManagerPassword("123456");

        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
        sslConnector.setPort(3001);

        server.setConnectors(new Connector[]{connector, sslConnector});

        WebAppContext context = new WebAppContext();
        context.setServer(server);

        context.setContextPath("/poc");
        context.addServlet(SSLServlet.class, "/hello2");
        context.addServlet(ConnectWSServlet.class, "/wsexample");

        ProtectionDomain protectionDomain = Application.class.getProtectionDomain();

        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        ContextHandlerCollection contexts = new ContextHandlerCollection();

        contexts.setHandlers(new Handler[]{context});


        server.setHandler(context);
        while (true) {
            try {
                server.start();
                server.join();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}