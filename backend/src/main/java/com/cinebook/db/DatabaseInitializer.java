package com.cinebook.db;

import com.cinebook.util.PasswordUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

public final class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void init() {
        Database.init();
        try (Connection connection = Database.getConnection()) {
            SqlScriptRunner.run(connection, "db/schema.sql");
            seedIfEmpty(connection);
        } catch (Exception e) {
            throw new IllegalStateException("Database initialization failed", e);
        }
    }

    private static void seedIfEmpty(Connection connection) throws Exception {
        if (hasAnyRows(connection, "users") || hasAnyRows(connection, "movies")) {
            return;
        }

        connection.setAutoCommit(false);
        try {
            insertUser(connection, "user", "123456", "user@example.com", "user");
            insertUser(connection, "admin", "1234", "admin@example.com", "admin");

            int interstellar = insertMovie(connection, "Interstellar",
                "https://picsum.photos/id/1000/300/450",
                "In the near future, Earth is becoming uninhabitable. A team of explorers travels through a wormhole to find a new home for humanity.");
            insertShowtimeWithSeats(connection, interstellar, "2023-10-15", "14:00", "Hall 1", 50);
            insertShowtimeWithSeats(connection, interstellar, "2023-10-15", "16:30", "Hall 2", 50);
            insertShowtimeWithSeats(connection, interstellar, "2023-10-15", "19:00", "Hall 1", 60);

            int inception = insertMovie(connection, "Inception",
                "https://picsum.photos/id/1015/300/450",
                "A skilled thief who steals secrets from within dreams is offered a chance at redemption: planting an idea instead of stealing one.");
            insertShowtimeWithSeats(connection, inception, "2023-10-15", "15:00", "Hall 3", 55);
            insertShowtimeWithSeats(connection, inception, "2023-10-15", "18:30", "Hall 4", 60);

            int shawshank = insertMovie(connection, "The Shawshank Redemption",
                "https://picsum.photos/id/1018/300/450",
                "A banker wrongly convicted forms a friendship in prison and finds hope and redemption.");
            insertShowtimeWithSeats(connection, shawshank, "2023-10-15", "10:00", "Hall 2", 45);
            insertShowtimeWithSeats(connection, shawshank, "2023-10-15", "13:30", "Hall 3", 45);

            int forrest = insertMovie(connection, "Forrest Gump",
                "https://picsum.photos/id/1025/300/450",
                "A man with a low IQ achieves remarkable things through persistence and kindness.");
            insertShowtimeWithSeats(connection, forrest, "2023-10-15", "11:00", "Hall 4", 45);
            insertShowtimeWithSeats(connection, forrest, "2023-10-15", "17:30", "Hall 1", 55);

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static boolean hasAnyRows(Connection connection, String table) throws Exception {
        String sql = "SELECT 1 FROM " + table + " LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next();
        }
    }

    private static void insertUser(Connection connection, String username, String password,
                                   String email, String role) throws Exception {
        String sql = "INSERT INTO users (username, password_hash, email, role, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, PasswordUtil.hash(password));
            statement.setString(3, email);
            statement.setString(4, role);
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        }
    }

    private static int insertMovie(Connection connection, String title, String posterUrl, String description)
        throws Exception {
        String sql = "INSERT INTO movies (title, poster_url, description, status, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, title);
            statement.setString(2, posterUrl);
            statement.setString(3, description);
            statement.setString(4, "active");
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new IllegalStateException("Failed to insert movie: " + title);
    }

    private static void insertShowtimeWithSeats(Connection connection, int movieId, String date,
                                                String time, String hall, double price) throws Exception {
        String sql = "INSERT INTO showtimes (movie_id, show_date, show_time, hall, price, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        int showtimeId;
        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, movieId);
            statement.setDate(2, Date.valueOf(LocalDate.parse(date)));
            statement.setTime(3, Time.valueOf(LocalTime.parse(time)));
            statement.setString(4, hall);
            statement.setDouble(5, price);
            statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    showtimeId = keys.getInt(1);
                } else {
                    throw new IllegalStateException("Failed to insert showtime for movie " + movieId);
                }
            }
        }

        String seatSql = "INSERT INTO showtime_seats (showtime_id, seat_index, status) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(seatSql)) {
            for (int i = 0; i < 64; i++) {
                statement.setInt(1, showtimeId);
                statement.setInt(2, i);
                statement.setString(3, "available");
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
