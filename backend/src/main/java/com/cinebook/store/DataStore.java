package com.cinebook.store;

import com.cinebook.model.Movie;
import com.cinebook.model.Order;
import com.cinebook.model.Showtime;
import com.cinebook.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DataStore {
    private static final List<Movie> MOVIES = new ArrayList<>();
    private static final List<User> USERS = new ArrayList<>();
    private static final List<Order> ORDERS = new ArrayList<>();
    private static final AtomicInteger MOVIE_ID = new AtomicInteger(1);
    private static final AtomicInteger USER_ID = new AtomicInteger(1);
    private static final AtomicInteger SHOWTIME_ID = new AtomicInteger(1);

    static {
        seedUsers();
        seedMovies();
        updateCounters();
    }

    public static List<Movie> getMovies() {
        return MOVIES;
    }

    public static List<User> getUsers() {
        return USERS;
    }

    public static List<Order> getOrders() {
        return ORDERS;
    }

    public static int nextMovieId() {
        return MOVIE_ID.getAndIncrement();
    }

    public static int nextUserId() {
        return USER_ID.getAndIncrement();
    }

    public static int nextShowtimeId() {
        return SHOWTIME_ID.getAndIncrement();
    }

    public static boolean[] createSeats() {
        boolean[] seats = new boolean[64];
        for (int i = 0; i < seats.length; i++) {
            seats[i] = true;
        }
        return seats;
    }

    private static void seedUsers() {
        USERS.add(new User(USER_ID.getAndIncrement(), "user", "123456", "user@example.com", "user"));
        USERS.add(new User(USER_ID.getAndIncrement(), "admin", "1234", "admin@example.com", "admin"));
    }

    private static void seedMovies() {
        List<Showtime> showtimes1 = new ArrayList<>();
        showtimes1.add(new Showtime(101, "2023-10-15", "14:00", "Hall 1", 50, createSeats()));
        showtimes1.add(new Showtime(102, "2023-10-15", "16:30", "Hall 2", 50, createSeats()));
        showtimes1.add(new Showtime(103, "2023-10-15", "19:00", "Hall 1", 60, createSeats()));
        MOVIES.add(new Movie(1, "Interstellar", "https://picsum.photos/id/1000/300/450",
            "In the near future, Earth is becoming uninhabitable. A team of explorers travels through a wormhole to find a new home for humanity.", showtimes1));

        List<Showtime> showtimes2 = new ArrayList<>();
        showtimes2.add(new Showtime(201, "2023-10-15", "15:00", "Hall 3", 55, createSeats()));
        showtimes2.add(new Showtime(202, "2023-10-15", "18:30", "Hall 4", 60, createSeats()));
        MOVIES.add(new Movie(2, "Inception", "https://picsum.photos/id/1015/300/450",
            "A skilled thief who steals secrets from within dreams is offered a chance at redemption: planting an idea instead of stealing one.", showtimes2));

        List<Showtime> showtimes3 = new ArrayList<>();
        showtimes3.add(new Showtime(301, "2023-10-15", "10:00", "Hall 2", 45, createSeats()));
        showtimes3.add(new Showtime(302, "2023-10-15", "13:30", "Hall 3", 45, createSeats()));
        MOVIES.add(new Movie(3, "The Shawshank Redemption", "https://picsum.photos/id/1018/300/450",
            "A banker wrongly convicted forms a friendship in prison and finds hope and redemption.", showtimes3));

        List<Showtime> showtimes4 = new ArrayList<>();
        showtimes4.add(new Showtime(401, "2023-10-15", "11:00", "Hall 4", 45, createSeats()));
        showtimes4.add(new Showtime(402, "2023-10-15", "17:30", "Hall 1", 55, createSeats()));
        MOVIES.add(new Movie(4, "Forrest Gump", "https://picsum.photos/id/1025/300/450",
            "A man with a low IQ achieves remarkable things through persistence and kindness.", showtimes4));
    }

    private static void updateCounters() {
        int maxMovieId = 0;
        int maxShowtimeId = 0;
        int maxUserId = 0;
        for (Movie movie : MOVIES) {
            if (movie.getId() > maxMovieId) {
                maxMovieId = movie.getId();
            }
            if (movie.getShowtimes() != null) {
                for (Showtime showtime : movie.getShowtimes()) {
                    if (showtime.getId() > maxShowtimeId) {
                        maxShowtimeId = showtime.getId();
                    }
                }
            }
        }
        for (User user : USERS) {
            if (user.getId() > maxUserId) {
                maxUserId = user.getId();
            }
        }
        MOVIE_ID.set(maxMovieId + 1);
        SHOWTIME_ID.set(maxShowtimeId + 1);
        USER_ID.set(maxUserId + 1);
    }
}
