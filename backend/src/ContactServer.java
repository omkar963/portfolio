import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ContactServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/contact/send", new ContactHandler());
        server.createContext("/api/contact/health", new HealthHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("🚀 Portfolio Backend Server started on port " + port);
        System.out.println("📍 Health check: http://localhost:" + port + "/api/contact/health");
        System.out.println("📧 Contact endpoint: http://localhost:" + port + "/api/contact/send");
        System.out.println("💡 Now open frontend/index.html in your browser!");
    }

    static class ContactHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORSHeaders(exchange);

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }

            if (!"POST".equals(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                String requestBody = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                System.out.println("=".repeat(60));
                System.out.println("🚀 NEW CONTACT FORM SUBMISSION");
                System.out.println("⏰ Timestamp: " + LocalDateTime.now());

                String name = extractValue(requestBody, "name");
                String email = extractValue(requestBody, "email");
                String subject = extractValue(requestBody, "subject");
                String message = extractValue(requestBody, "message");

                System.out.println("👤 Name: " + name);
                System.out.println("📧 Email: " + email);
                System.out.println("📝 Subject: " + subject);
                System.out.println("💬 Message: " + message);
                System.out.println("=".repeat(60));

                String response = "{\"success\":true,\"message\":\"Thank you for your message! I'll get back to you soon.\",\"timestamp\":\"" + LocalDateTime.now() + "\"}";
                sendResponse(exchange, 200, response);

            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                String response = "{\"success\":false,\"message\":\"Sorry, there was an error.\",\"timestamp\":\"" + LocalDateTime.now() + "\"}";
                sendResponse(exchange, 500, response);
            }
        }

        private String extractValue(String json, String key) {
            try {
                String search = "\"" + key + "\":\"";
                int start = json.indexOf(search) + search.length();
                int end = json.indexOf("\"", start);
                return json.substring(start, end);
            } catch (Exception e) {
                return "Unknown";
            }
        }
    }

    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            setCORSHeaders(exchange);
            String response = "{\"status\":\"UP\",\"service\":\"Portfolio Backend\",\"timestamp\":\"" + LocalDateTime.now() + "\"}";
            sendResponse(exchange, 200, response);
        }
    }

    private static void setCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Content-Type", "application/json");
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendResponse(exchange, statusCode, "{\"error\":\"" + message + "\"}");
    }
}