/**
 * Showtime class representing movie screening schedules in the cinema system
 * Contains information about which movie is shown in which theater and when
 */
package Model;

import java.time.LocalDateTime;

public class Showtime {
    private int id;                   // Unique identifier for the showtime record
    private int movieId;              // ID of the associated movie
    private int theaterId;            // ID of the associated theater
    private LocalDateTime startTime;  // Screening start time
    private LocalDateTime endTime;    // Screening end time

    /**
     * Default constructor for Showtime class
     */
    public Showtime() {}

    /**
     * Parameterized constructor to create a complete showtime object
     * @param id Showtime record ID
     * @param movieId Associated movie ID
     * @param theaterId Associated theater ID
     * @param startTime Screening start time
     * @param endTime Screening end time
     */
    public Showtime(int id, int movieId, int theaterId, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter and setter methods
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public int getTheaterId() { return theaterId; }
    public void setTheaterId(int theaterId) { this.theaterId = theaterId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}

