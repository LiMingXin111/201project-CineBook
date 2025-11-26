package Dao;

import Model.Movie;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    private List<Movie> movies = new ArrayList<>();

    /**
     * Adds a new movie to the collection
     *
     * @param movie The movie object to be added
     */
    public void add(Movie movie) {
        movies.add(movie);
    }

    /**
     * Retrieves all movies from the collection
     *
     * @return List of all movies
     */
    public List<Movie> getAll() {
        return movies;
    }

    /**
     * Finds a movie by its unique identifier
     *
     * @param id The unique identifier of the movie
     * @return The movie object if found, null otherwise
     */
    public Movie getById(int id) {
        return movies.stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }
}

