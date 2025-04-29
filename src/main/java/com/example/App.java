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
        int port = Config.getPort();
        Server server = new Server(port);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.setContextPath("/");
        handler.addServlet(new ServletHolder(new HelloServlet()), "/hello");
        handler.addServlet(new ServletHolder(new UserServlet()), "/users/*");
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

    public static class UserServlet extends HttpServlet {
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final Map<String, User> userStore = new java.util.concurrent.ConcurrentHashMap<>();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json");
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all users
                resp.getWriter().write(objectMapper.writeValueAsString(userStore.values()));
            } else {
                // Get user by id
                String id = pathInfo.substring(1);
                User user = userStore.get(id);
                if (user == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{}\n");
                } else {
                    resp.getWriter().write(objectMapper.writeValueAsString(user));
                }
            }
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && !pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            User user = objectMapper.readValue(req.getInputStream(), User.class);
            if (user.getId() == null || user.getId().isEmpty()) {
                user.setId(java.util.UUID.randomUUID().toString());
            }
            userStore.put(user.getId(), user);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(user));
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String id = pathInfo.substring(1);
            User user = objectMapper.readValue(req.getInputStream(), User.class);
            user.setId(id);
            if (!userStore.containsKey(id)) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            userStore.put(id, user);
            resp.setContentType("application/json");
            resp.getWriter().write(objectMapper.writeValueAsString(user));
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            String id = pathInfo.substring(1);
            User removed = userStore.remove(id);
            if (removed == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}
