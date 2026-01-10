package com.cinebook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class SeatDao {
    public boolean[] getSeatMap(Connection connection, int showtimeId) throws Exception {
        boolean[] seats = new boolean[64];
        Arrays.fill(seats, false);

        String sql = "SELECT seat_index, status FROM showtime_seats WHERE showtime_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, showtimeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int index = resultSet.getInt("seat_index");
                    if (index >= 0 && index < seats.length) {
                        String status = resultSet.getString("status");
                        seats[index] = "available".equalsIgnoreCase(status);
                    }
                }
            }
        }
        return seats;
    }

    public void insertSeats(Connection connection, int showtimeId) throws Exception {
        String sql = "INSERT INTO showtime_seats (showtime_id, seat_index, status) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < 64; i++) {
                statement.setInt(1, showtimeId);
                statement.setInt(2, i);
                statement.setString(3, "available");
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public boolean lockSeatsIfAvailable(Connection connection, int showtimeId, List<Integer> seatIndices)
        throws Exception {
        if (seatIndices.isEmpty()) {
            return false;
        }

        String placeholders = buildPlaceholders(seatIndices.size());
        String sql = "SELECT seat_index, status FROM showtime_seats " +
            "WHERE showtime_id = ? AND seat_index IN (" + placeholders + ") FOR UPDATE";
        boolean[] seen = new boolean[64];
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, showtimeId);
            for (int i = 0; i < seatIndices.size(); i++) {
                statement.setInt(i + 2, seatIndices.get(i));
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int index = resultSet.getInt("seat_index");
                    String status = resultSet.getString("status");
                    if (index < 0 || index >= seen.length || !"available".equalsIgnoreCase(status)) {
                        return false;
                    }
                    seen[index] = true;
                }
            }
        }

        for (Integer index : seatIndices) {
            if (index == null || index < 0 || index >= seen.length || !seen[index]) {
                return false;
            }
        }
        return true;
    }

    public void markSeatsSold(Connection connection, int showtimeId, List<Integer> seatIndices) throws Exception {
        if (seatIndices.isEmpty()) {
            return;
        }
        String placeholders = buildPlaceholders(seatIndices.size());
        String sql = "UPDATE showtime_seats SET status = 'sold' " +
            "WHERE showtime_id = ? AND seat_index IN (" + placeholders + ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, showtimeId);
            for (int i = 0; i < seatIndices.size(); i++) {
                statement.setInt(i + 2, seatIndices.get(i));
            }
            statement.executeUpdate();
        }
    }

    public void deleteByShowtime(Connection connection, int showtimeId) throws Exception {
        String sql = "DELETE FROM showtime_seats WHERE showtime_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, showtimeId);
            statement.executeUpdate();
        }
    }

    private String buildPlaceholders(int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append('?');
        }
        return builder.toString();
    }
}
