package com.cinebook.service;

import com.cinebook.dao.OrderDao;
import com.cinebook.dao.SeatDao;
import com.cinebook.db.Database;
import com.cinebook.model.Movie;
import com.cinebook.model.Order;
import com.cinebook.model.Showtime;
import com.cinebook.model.User;

import java.sql.Connection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OrderService {
    private final OrderDao orderDao = new OrderDao();
    private final SeatDao seatDao = new SeatDao();

    public List<Order> getAllOrders() {
        return orderDao.getAll();
    }

    public List<Order> getOrdersByUserId(int userId) {
        return orderDao.getByUserId(userId);
    }

    public Order placeOrder(User user, Movie movie, Showtime showtime, List<Integer> seatIndices) {
        if (user == null || movie == null || showtime == null) {
            throw new OrderException(400, "Invalid order details");
        }
        if (seatIndices == null || seatIndices.isEmpty()) {
            throw new OrderException(400, "Seat selection is required");
        }

        List<Integer> normalizedSeats = normalizeSeats(seatIndices);
        if (normalizedSeats.isEmpty()) {
            throw new OrderException(400, "Seat selection is required");
        }

        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                boolean available = seatDao.lockSeatsIfAvailable(connection, showtime.getId(), normalizedSeats);
                if (!available) {
                    throw new OrderException(409, "Selected seat is unavailable");
                }

                Order order = buildOrder(user, movie, showtime, normalizedSeats);
                orderDao.insert(connection, order);
                orderDao.insertOrderSeats(connection, order.getId(), normalizedSeats);
                seatDao.markSeatsSold(connection, showtime.getId(), normalizedSeats);

                connection.commit();
                return order;
            } catch (OrderException e) {
                connection.rollback();
                throw e;
            } catch (Exception e) {
                connection.rollback();
                throw new OrderException(500, "Failed to create order");
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (OrderException e) {
            throw e;
        } catch (Exception e) {
            throw new OrderException(500, "Failed to create order");
        }
    }

    private List<Integer> normalizeSeats(List<Integer> seatIndices) {
        Set<Integer> unique = new LinkedHashSet<>();
        for (Integer index : seatIndices) {
            if (index == null || index < 0 || index >= 64) {
                throw new OrderException(400, "Invalid seat index");
            }
            unique.add(index);
        }
        return new ArrayList<>(unique);
    }

    private Order buildOrder(User user, Movie movie, Showtime showtime, List<Integer> seatIndices) {
        String orderId = "ORD" + System.currentTimeMillis();
        String seatNumbers = formatSeatNumbers(seatIndices);
        double total = seatIndices.size() * showtime.getPrice();
        String showtimeLabel = showtime.getDate() + " " + showtime.getTime();
        Instant now = Instant.now();

        Order order = new Order();
        order.setId(orderId);
        order.setUserId(user.getId());
        order.setMovieId(movie.getId());
        order.setMovieTitle(movie.getTitle());
        order.setShowtimeId(showtime.getId());
        order.setShowtime(showtimeLabel);
        order.setHall(showtime.getHall());
        order.setSeats(seatNumbers);
        order.setSeatIndices(seatIndices);
        order.setPrice(showtime.getPrice());
        order.setTotal(total);
        order.setOrderDate(now.toString());
        order.setStatus("paid");
        return order;
    }

    private String formatSeatNumbers(List<Integer> indices) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < indices.size(); i++) {
            int index = indices.get(i);
            char row = (char) ('A' + (index / 8));
            int seatNumber = (index % 8) + 1;
            builder.append(row).append(seatNumber);
            if (i < indices.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }
}
