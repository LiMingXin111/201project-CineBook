package com.cinebook.controller;

import com.cinebook.model.Movie;
import com.cinebook.model.Order;
import com.cinebook.service.MovieService;
import com.cinebook.service.OrderService;
import com.cinebook.util.JsonUtil;
import com.cinebook.server.Request;
import com.cinebook.server.Response;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportController {
    private final OrderService orderService = new OrderService();
    private final MovieService movieService = new MovieService();

    public Response handleRequest(Request request) {
        if ("GET".equals(request.getMethod()) && "/api/reports".equals(request.getPath())) {
            return getReports();
        }
        return new Response(404, "text/plain", "Not Found");
    }

    private Response getReports() {
        List<Order> orders = orderService.getAllOrders();
        ReportResponse report = buildReport(orders);
        return new Response(200, "application/json", JsonUtil.toJson(report));
    }

    private ReportResponse buildReport(List<Order> orders) {
        ReportResponse report = new ReportResponse();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.minusDays(29);

        Map<Integer, Double> totalsByMovie = new HashMap<>();

        for (Order order : orders) {
            LocalDate orderDate = parseOrderDate(order.getOrderDate());
            if (orderDate == null) {
                continue;
            }
            if (!orderDate.isAfter(today) && !orderDate.isBefore(today)) {
                report.dailySales += order.getTotal();
            }
            if (!orderDate.isBefore(weekStart)) {
                report.weeklySales += order.getTotal();
            }
            if (!orderDate.isBefore(monthStart)) {
                report.monthlySales += order.getTotal();
            }

            totalsByMovie.put(order.getMovieId(),
                totalsByMovie.getOrDefault(order.getMovieId(), 0.0) + order.getTotal());
        }

        Map<Integer, Movie> moviesById = new HashMap<>();
        for (Movie movie : movieService.getAllMovies()) {
            moviesById.put(movie.getId(), movie);
        }

        List<MovieSales> salesList = new ArrayList<>();
        for (Movie movie : moviesById.values()) {
            MovieSales movieSales = new MovieSales();
            movieSales.movieId = movie.getId();
            movieSales.title = movie.getTitle();
            movieSales.poster = movie.getPoster();
            movieSales.total = totalsByMovie.getOrDefault(movie.getId(), 0.0);
            salesList.add(movieSales);
        }

        for (Map.Entry<Integer, Double> entry : totalsByMovie.entrySet()) {
            if (!moviesById.containsKey(entry.getKey())) {
                MovieSales movieSales = new MovieSales();
                movieSales.movieId = entry.getKey();
                movieSales.title = "Unknown";
                movieSales.poster = null;
                movieSales.total = entry.getValue();
                salesList.add(movieSales);
            }
        }

        salesList.sort((a, b) -> {
            int totalCompare = Double.compare(b.total, a.total);
            if (totalCompare != 0) {
                return totalCompare;
            }
            return String.valueOf(a.title).compareToIgnoreCase(String.valueOf(b.title));
        });

        report.movieSales = salesList;
        return report;
    }

    private LocalDate parseOrderDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Instant instant = Instant.parse(value);
            return instant.atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return dateTime.toLocalDate();
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private static class ReportResponse {
        public double dailySales;
        public double weeklySales;
        public double monthlySales;
        public List<MovieSales> movieSales;
    }

    private static class MovieSales {
        public int movieId;
        public String title;
        public String poster;
        public double total;
    }
}
