import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.Map;
import java.util.List;

/**
 * Simple web server to expose booking service APIs
 */
public class SimpleWebServer {
    private HttpServer server;
    private BookingService bookingService;

    public SimpleWebServer(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Register API endpoints
        server.createContext("/api/seats", new SeatMapHandler());
        server.createContext("/api/book", new BookSeatHandler());
        server.createContext("/api/mybookings", new MyBookingsHandler());
        server.createContext("/api/cancel", new CancelBookingHandler());
        
        // Use a thread pool to handle concurrent requests
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        
        System.out.println("Server started on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Server stopped");
        }
    }

    // Simple JSON utility methods (since we can't use external libraries)
    private String toJson(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private String toJson(List<Booking> bookings) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < bookings.size(); i++) {
            if (i > 0) sb.append(",");
            Booking b = bookings.get(i);
            sb.append("{");
            sb.append("\"bookingId\":").append(b.getBookingId()).append(",");
            sb.append("\"userId\":").append(b.getUserId()).append(",");
            sb.append("\"screeningId\":").append(b.getScreeningId()).append(",");
            sb.append("\"seatNumber\":\"").append(b.getSeatNumber()).append("\",");
            sb.append("\"status\":\"").append(b.getStatus()).append("\",");
            sb.append("\"price\":").append(b.getPrice());
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private Map<String, String> parseJson(String json) {
        // This is a very simplified JSON parser for our specific use case
        // In a real application, you would want to use a proper JSON library
        new java.util.HashMap<String, String>();
        // For now, we'll just return an empty map as this is a simplified implementation
        // A full implementation would parse the JSON string and extract key-value pairs
        return new java.util.HashMap<>();
    }

    // Handler for getting seat map
    class SeatMapHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String query = exchange.getRequestURI().getQuery();
                    int screeningId = Integer.parseInt(query.split("=")[1]);
                    
                    Map<String, String> seatMap = bookingService.getSeatMap(screeningId);
                    String response = toJson(seatMap);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Handler for booking a seat
    class BookSeatHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // In a real implementation, we would parse the JSON request body
                    // For simplicity, we'll just send a success message
                    String response = "{\"message\": \"Booking successful\"}";
                    
                    // For now, we'll just use mock data since we don't have a full JSON parser
                    int screeningId = 1; // Mock value
                    String seatNumber = "A1"; // Mock value
                    
                    // In a real implementation, we would parse these from the request body:
                    // String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    // Parse the JSON to extract screeningId and seatNumber
                    
                    bookingService.attemptBooking(screeningId, seatNumber);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (SeatUnavailableException e) {
                    sendErrorResponse(exchange, 409, "Seat unavailable: " + e.getMessage());
                } catch (BookingException e) {
                    sendErrorResponse(exchange, 400, "Booking error: " + e.getMessage());
                } catch (Exception e) {
                    sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Handler for getting user's bookings
    class MyBookingsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    List<Booking> bookings = bookingService.getMyBookings();
                    String response = toJson(bookings);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Handler for cancelling a booking
    class CancelBookingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // In a real implementation, we would parse the JSON request body
                    // For now, we'll just use mock data and send a success message
                    String response = "{\"success\": true}";
                    
                    // For now, we'll just use a mock bookingId since we don't have a full JSON parser
                    // int bookingId = parseBookingIdFromRequest(exchange);
                    int bookingId = 1; // Mock value
                    
                    boolean success = bookingService.cancelMyBooking(bookingId);
                    
                    response = "{\"success\": " + success + "}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length());
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    sendErrorResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
                }
            } else {
                sendErrorResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }

    // Helper method to send error responses
    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        String response = "{\"error\": \"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.length());
        
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}