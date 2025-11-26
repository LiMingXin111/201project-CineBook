/**
 * Service class for managing showtime operations
 * Provides functionality for adding, retrieving, and validating showtimes
 */
package Service;

import Dao.ShowtimeDAO;
import Exception.ShowtimeConflictException;
import Model.Showtime;

import java.time.LocalDateTime;
import java.util.List;

public class ShowtimeService implements IShowtimeService {

    private ShowtimeDAO showtimeDAO;

    /**
     * Constructor for ShowtimeService
     * @param showtimeDAO Data access object for showtime operations
     */
    public ShowtimeService(ShowtimeDAO showtimeDAO) {
        this.showtimeDAO = showtimeDAO;
    }

    /**
     * Adds a new showtime after checking for scheduling conflicts
     * @param newShowtime The showtime to be added
     * @throws ShowtimeConflictException If the new showtime conflicts with existing ones
     */
    @Override
    public void addShowtime(Showtime newShowtime) throws ShowtimeConflictException {

        List<Showtime> existing = showtimeDAO.getByTheater(newShowtime.getTheaterId());

        for (Showtime s : existing) {
            if (isConflict(s, newShowtime)) {
                throw new ShowtimeConflictException(
                        "Showtime conflict detected between " +
                                s.getStartTime() + " - " + s.getEndTime()
                );
            }
        }

        showtimeDAO.add(newShowtime);
    }

    /**
     * Retrieves all showtimes for a specific theater
     * @param theaterId The unique identifier of the theater
     * @return List of showtimes for the specified theater
     */
    @Override
    public List<Showtime> getShowtimesByTheater(int theaterId) {
        return showtimeDAO.getByTheater(theaterId);
    }

    /**
     * Retrieves all showtimes from the system
     * @return List of all showtimes
     */
    @Override
    public List<Showtime> getAllShowtimes() {
        return showtimeDAO.getAll();
    }

    /**
     * Checks if two showtimes have scheduling conflicts
     * @param a First showtime to compare
     * @param b Second showtime to compare
     * @return true if there is a time overlap, false otherwise
     */
    private boolean isConflict(Showtime a, Showtime b) {
        return a.getStartTime().isBefore(b.getEndTime())
                && b.getStartTime().isBefore(a.getEndTime());
    }
}


