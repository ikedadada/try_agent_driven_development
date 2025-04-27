package com.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class App {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new HelloServlet()), "/hello");
        server.setHandler(handler);
        server.start();
        server.join();
    }

    public static class HelloServlet extends HttpServlet {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json");
            String name = req.getParameter("name");
            if (name == null || name.isEmpty()) {
                name = "World";
            }
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hello " + name + "!");
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        }
    }
}
