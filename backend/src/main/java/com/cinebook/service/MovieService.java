package com.cinebook.service;

import com.cinebook.dao.MovieDao;
import com.cinebook.model.Movie;
import com.cinebook.model.Showtime;

import java.util.List;

public class MovieService {
    private final MovieDao movieDao = new MovieDao();

    public List<Movie> getAllMovies() {
        return movieDao.getAll();
    }

    public Movie getMovieById(int id) {
        return movieDao.getById(id);
    }

    public Movie addMovie(Movie movie) {
        return movieDao.insert(movie);
    }

    public Movie updateMovie(Movie movie) {
        return movieDao.update(movie);
    }

    public boolean deleteMovie(int id) {
        return movieDao.delete(id);
    }

    public Showtime getShowtimeById(Movie movie, int showtimeId) {
        if (movie == null || movie.getShowtimes() == null) {
            return null;
        }
        for (Showtime showtime : movie.getShowtimes()) {
            if (showtime.getId() == showtimeId) {
                return showtime;
            }
        }
        return null;
    }
}
