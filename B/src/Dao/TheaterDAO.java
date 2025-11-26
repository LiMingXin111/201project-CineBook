/**
 * Data Access Object for Theater entities
 * Provides storage and retrieval operations for theater data
 */
package Dao;

import Model.Theater;
import java.util.ArrayList;
import java.util.List;

public class TheaterDAO {
    private List<Theater> theaters = new ArrayList<>();

    /**
     * Adds a new theater to the collection
     *
     * @param theater The theater object to be added
     */
    public void add(Theater theater) {
        theaters.add(theater);
    }

    /**
     * Retrieves all theaters from the collection
     *
     * @return List of all theaters
     */
    public List<Theater> getAll() {
        return theaters;
    }

    /**
     * Finds a theater by its unique identifier
     *
     * @param id The unique identifier of the theater
     * @return The theater object if found, null otherwise
     */
    public Theater getById(int id) {
        return theaters.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
