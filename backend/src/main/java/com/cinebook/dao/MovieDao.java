package com.cinebook.dao;

import com.cinebook.db.Database;
import com.cinebook.model.Movie;
import com.cinebook.model.Showtime;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MovieDao {
    private final SeatDao seatDao = new SeatDao();

    public List<Movie> getAll() {
        try (Connection connection = Database.getConnection()) {
            return getAll(connection);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load movies", e);
        }
    }

    public Movie getById(int id) {
        String sql = "SELECT id, title, poster_url, description FROM movies WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Movie movie = mapMovie(resultSet);
                    movie.setShowtimes(getShowtimes(connection, movie.getId()));
                    return movie;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load movie by id", e);
        }
        return null;
    }

    public Movie insert(Movie movie) {
        String sql = "INSERT INTO movies (title, poster_url, description, status, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, movie.getTitle());
                    statement.setString(2, movie.getPoster());
                    statement.setString(3, movie.getDescription());
                    statement.setString(4, "active");
                    statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (keys.next()) {
                            movie.setId(keys.getInt(1));
                        }
                    }
                }

                insertShowtimes(connection, movie);
                connection.commit();
                return movie;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to insert movie", e);
        }
    }

    public Movie update(Movie movie) {
        String sql = "UPDATE movies SET title = ?, poster_url = ?, description = ? WHERE id = ?";
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                int updated;
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, movie.getTitle());
                    statement.setString(2, movie.getPoster());
                    statement.setString(3, movie.getDescription());
                    statement.setInt(4, movie.getId());
                    updated = statement.executeUpdate();
                }

                if (updated == 0) {
                    connection.rollback();
                    return null;
                }

                if (movie.getShowtimes() != null && !movie.getShowtimes().isEmpty()) {
                    deleteShowtimes(connection, movie.getId());
                    insertShowtimes(connection, movie);
                }
                connection.commit();
                return getById(movie.getId());
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to update movie", e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to delete movie", e);
        }
    }

    public List<Showtime> getShowtimesByMovieId(int movieId) {
        try (Connection connection = Database.getConnection()) {
            return getShowtimes(connection, movieId);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load showtimes", e);
        }
    }

    private List<Movie> getAll(Connection connection) throws Exception {
        String sql = "SELECT id, title, poster_url, description FROM movies ORDER BY id";
        List<Movie> movies = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Movie movie = mapMovie(resultSet);
                movie.setShowtimes(getShowtimes(connection, movie.getId()));
                movies.add(movie);
            }
        }
        return movies;
    }

    private List<Showtime> getShowtimes(Connection connection, int movieId) throws Exception {
        String sql = "SELECT id, show_date, show_time, hall, price FROM showtimes WHERE movie_id = ? ORDER BY id";
        List<Showtime> showtimes = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Showtime showtime = new Showtime();
                    showtime.setId(resultSet.getInt("id"));
                    Date date = resultSet.getDate("show_date");
                    Time time = resultSet.getTime("show_time");
                    showtime.setDate(date != null ? date.toString() : null);
                    showtime.setTime(time != null ? time.toLocalTime().toString() : null);
                    showtime.setHall(resultSet.getString("hall"));
                    showtime.setPrice(resultSet.getDouble("price"));
                    showtime.setSeats(seatDao.getSeatMap(connection, showtime.getId()));
                    showtimes.add(showtime);
                }
            }
        }
        return showtimes;
    }

    private void insertShowtimes(Connection connection, Movie movie) throws Exception {
        if (movie.getShowtimes() == null) {
            return;
        }
        String sql = "INSERT INTO showtimes (movie_id, show_date, show_time, hall, price, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        for (Showtime showtime : movie.getShowtimes()) {
            try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, movie.getId());
                statement.setDate(2, Date.valueOf(LocalDate.parse(showtime.getDate())));
                statement.setTime(3, Time.valueOf(LocalTime.parse(showtime.getTime())));
                statement.setString(4, showtime.getHall());
                double price = showtime.getPrice() <= 0 ? 50 : showtime.getPrice();
                statement.setDouble(5, price);
                statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        showtime.setId(keys.getInt(1));
                    }
                }
            }
            seatDao.insertSeats(connection, showtime.getId());
        }
    }

    private void deleteShowtimes(Connection connection, int movieId) throws Exception {
        String selectSql = "SELECT id FROM showtimes WHERE movie_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setInt(1, movieId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int showtimeId = resultSet.getInt("id");
                    seatDao.deleteByShowtime(connection, showtimeId);
                }
            }
        }

        String deleteSql = "DELETE FROM showtimes WHERE movie_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setInt(1, movieId);
            statement.executeUpdate();
        }
    }

    private Movie mapMovie(ResultSet resultSet) throws Exception {
        Movie movie = new Movie();
        movie.setId(resultSet.getInt("id"));
        movie.setTitle(resultSet.getString("title"));
        movie.setPoster(resultSet.getString("poster_url"));
        movie.setDescription(resultSet.getString("description"));
        return movie;
    }
}
