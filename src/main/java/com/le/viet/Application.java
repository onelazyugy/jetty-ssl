package com.le.viet;

import com.le.viet.servlet.SSLServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.security.ProtectionDomain;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        //runServer();
        setupSSL();
    }

    private static void runServer(){
        try {
            //SpringApplication.run(Application.class, args);
            System.out.println("Hi");
            Server server = new Server(3001);
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
        connector.setPort(9999);

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(Application.class.getResource("/keystore.jks").toExternalForm());

        sslContextFactory.setKeyStorePassword("123456");
        sslContextFactory.setKeyManagerPassword("123456");

        ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
        sslConnector.setPort(9998);

        server.setConnectors(new Connector[]{connector, sslConnector});

        WebAppContext context = new WebAppContext();
        context.setServer(server);
        //
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        server.setHandler(handler);
        //
        context.setContextPath("/poc");


        //
        //ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        //handler.setContextPath("/PS-DeviceManagerClient/ws");
        //server.setHandler(handler);
        handler.addServlet(new ServletHolder(new SSLServlet()), "/hello");

        //
        //Server server = new Server(8083);
        //ServletContextHandler handler = new ServletContextHandler(server, "/PS-DeviceManagerClient/ws");
        //ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        //handler.setContextPath("/PS-DeviceManagerClient/ws");
        //server.setHandler(handler);
        //handler.addServlet(new ServletHolder(new PinPadSocketServlet()),"/WebSocket");
        //handler.addServlet(new ServletHolder(new HelloServlet()),"/hello");
        //

        ProtectionDomain protectionDomain = Application.class.getProtectionDomain();

        URL location = protectionDomain.getCodeSource().getLocation();
        context.setWar(location.toExternalForm());

        server.setHandler(context);
        while (true) {
            try {
                server.start();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            System.in.read();
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}