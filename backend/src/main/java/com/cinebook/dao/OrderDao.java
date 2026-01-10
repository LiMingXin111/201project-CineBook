package com.cinebook.dao;

import com.cinebook.db.Database;
import com.cinebook.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    public List<Order> getAll() {
        String sql = "SELECT id, user_id, movie_id, movie_title, showtime_id, showtime_label, " +
            "hall, seats_label, price, total, status, created_at FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(mapRow(resultSet));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load orders", e);
        }
        return orders;
    }

    public List<Order> getByUserId(int userId) {
        String sql = "SELECT id, user_id, movie_id, movie_title, showtime_id, showtime_label, " +
            "hall, seats_label, price, total, status, created_at FROM orders WHERE user_id = ? " +
            "ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapRow(resultSet));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load orders for user", e);
        }
        return orders;
    }

    public void insert(Connection connection, Order order) throws Exception {
        String sql = "INSERT INTO orders (id, user_id, movie_id, movie_title, showtime_id, showtime_label, " +
            "hall, seats_label, price, total, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, order.getId());
            statement.setInt(2, order.getUserId());
            statement.setInt(3, order.getMovieId());
            statement.setString(4, order.getMovieTitle());
            statement.setInt(5, order.getShowtimeId());
            statement.setString(6, order.getShowtime());
            statement.setString(7, order.getHall());
            statement.setString(8, order.getSeats());
            statement.setDouble(9, order.getPrice());
            statement.setDouble(10, order.getTotal());
            statement.setString(11, order.getStatus());
            Timestamp createdAt = Timestamp.from(Instant.now());
            if (order.getOrderDate() != null) {
                try {
                    createdAt = Timestamp.from(Instant.parse(order.getOrderDate()));
                } catch (Exception ignored) {
                    createdAt = Timestamp.from(Instant.now());
                }
            }
            statement.setTimestamp(12, createdAt);
            statement.executeUpdate();
        }
    }

    public void insertOrderSeats(Connection connection, String orderId, List<Integer> seatIndices) throws Exception {
        String sql = "INSERT INTO order_seats (order_id, seat_index) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Integer index : seatIndices) {
                statement.setString(1, orderId);
                statement.setInt(2, index);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private Order mapRow(ResultSet resultSet) throws Exception {
        Order order = new Order();
        order.setId(resultSet.getString("id"));
        order.setUserId(resultSet.getInt("user_id"));
        order.setMovieId(resultSet.getInt("movie_id"));
        order.setMovieTitle(resultSet.getString("movie_title"));
        order.setShowtimeId(resultSet.getInt("showtime_id"));
        order.setShowtime(resultSet.getString("showtime_label"));
        order.setHall(resultSet.getString("hall"));
        order.setSeats(resultSet.getString("seats_label"));
        order.setPrice(resultSet.getDouble("price"));
        order.setTotal(resultSet.getDouble("total"));
        order.setStatus(resultSet.getString("status"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            order.setOrderDate(createdAt.toInstant().toString());
        } else {
            order.setOrderDate(resultSet.getString("created_at"));
        }
        return order;
    }
}
