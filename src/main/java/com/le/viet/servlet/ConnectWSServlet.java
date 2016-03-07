package com.le.viet.servlet;

import com.le.viet.ws.HelloWebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by associate on 3/7/16.
 */
@WebServlet(name = "WebSocket Servlet", urlPatterns = { "/wsexample" })
public class ConnectWSServlet extends WebSocketServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println("<h1>ConnectWSServlet Servlet</h1>");
    }

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(30000);
        factory.register(HelloWebSocket.class);
    }
}
