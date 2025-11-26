/**
 * Data Access Object for Showtime entities
 * Provides storage and retrieval operations for showtime data
 */
package Dao;

import Model.Showtime;
import java.util.ArrayList;
import java.util.List;

public class ShowtimeDAO {
    private List<Showtime> showtimes = new ArrayList<>();

    /**
     * Adds a new showtime to the collection
     *
     * @param showtime The showtime object to be added
     */
    public void add(Showtime showtime) {
        showtimes.add(showtime);
    }

    /**
     * Retrieves all showtimes for a specific theater
     *
     * @param theaterId The unique identifier of the theater
     * @return List of showtimes associated with the specified theater
     */
    public List<Showtime> getByTheater(int theaterId) {
        List<Showtime> result = new ArrayList<>();
        for (Showtime s : showtimes) {
            if (s.getTheaterId() == theaterId) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Retrieves all showtimes from the collection
     *
     * @return List of all showtimes
     */
    public List<Showtime> getAll() {
        return showtimes;
    }
}
