package com.cinebook.controller;

import com.cinebook.model.Movie;
import com.cinebook.model.Order;
import com.cinebook.model.Showtime;
import com.cinebook.model.User;
import com.cinebook.server.Request;
import com.cinebook.server.Response;
import com.cinebook.service.MovieService;
import com.cinebook.service.OrderException;
import com.cinebook.service.OrderService;
import com.cinebook.service.UserService;
import com.cinebook.util.JsonUtil;

import java.util.List;

public class OrderController {
    private final OrderService orderService = new OrderService();
    private final MovieService movieService = new MovieService();
    private final UserService userService = new UserService();

    public Response handleRequest(Request request) {
        if ("GET".equals(request.getMethod()) && "/api/orders".equals(request.getPath())) {
            return getOrders(request);
        }
        if ("POST".equals(request.getMethod()) && "/api/orders".equals(request.getPath())) {
            return createOrder(request);
        }
        return new Response(404, "text/plain", "Not Found");
    }

    private Response getOrders(Request request) {
        String userIdValue = request.getQueryParam("userId");
        if (userIdValue == null) {
            if (isAdmin(request)) {
                return new Response(200, "application/json", JsonUtil.toJson(orderService.getAllOrders()));
            }
            return badRequest("Invalid request", "userId is required");
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdValue);
        } catch (NumberFormatException e) {
            return badRequest("Invalid request", "userId must be a number");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return new Response(404, "application/json",
                JsonUtil.toJson(new ErrorResponse("Not Found", "User not found")));
        }

        List<Order> orders = orderService.getOrdersByUserId(userId);
        return new Response(200, "application/json", JsonUtil.toJson(orders));
    }

    private Response createOrder(Request request) {
        if (request.getBody() == null || request.getBody().trim().isEmpty()) {
            return badRequest("Invalid request", "Request body is required");
        }

        CreateOrderRequest createRequest;
        try {
            createRequest = JsonUtil.fromJson(request.getBody(), CreateOrderRequest.class);
        } catch (Exception e) {
            return badRequest("Invalid request", "Malformed JSON");
        }

        if (createRequest == null || createRequest.userId <= 0 || createRequest.movieId <= 0
            || createRequest.showtimeId <= 0 || createRequest.seatIndices == null
            || createRequest.seatIndices.isEmpty()) {
            return badRequest("Invalid request", "Missing required fields");
        }

        User user = userService.getById(createRequest.userId);
        if (user == null) {
            return new Response(404, "application/json",
                JsonUtil.toJson(new ErrorResponse("Not Found", "User not found")));
        }
        if ("admin".equalsIgnoreCase(user.getRole())) {
            return new Response(403, "application/json",
                JsonUtil.toJson(new ErrorResponse("Forbidden", "Admin users cannot purchase tickets")));
        }

        Movie movie = movieService.getMovieById(createRequest.movieId);
        if (movie == null) {
            return new Response(404, "application/json",
                JsonUtil.toJson(new ErrorResponse("Not Found", "Movie not found")));
        }

        Showtime showtime = movieService.getShowtimeById(movie, createRequest.showtimeId);
        if (showtime == null) {
            return new Response(404, "application/json",
                JsonUtil.toJson(new ErrorResponse("Not Found", "Showtime not found")));
        }

        try {
            Order order = orderService.placeOrder(user, movie, showtime, createRequest.seatIndices);
            return new Response(201, "application/json", JsonUtil.toJson(order));
        } catch (OrderException e) {
            return new Response(e.getStatusCode(), "application/json",
                JsonUtil.toJson(new ErrorResponse("Order Error", e.getMessage())));
        }
    }

    private Response badRequest(String error, String message) {
        return new Response(400, "application/json",
            JsonUtil.toJson(new ErrorResponse(error, message)));
    }

    private boolean isAdmin(Request request) {
        return "true".equals(request.getHeader("Admin"));
    }

    private static class CreateOrderRequest {
        public int userId;
        public int movieId;
        public int showtimeId;
        public List<Integer> seatIndices;
    }

    private static class ErrorResponse {
        public String error;
        public String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
