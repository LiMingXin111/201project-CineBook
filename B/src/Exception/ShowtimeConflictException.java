package Exception;

/**
 * Exception thrown when attempting to create a showtime
 * that overlaps with an existing one in the same theater.
 */
public class ShowtimeConflictException extends RuntimeException {
    public ShowtimeConflictException(String message) {
        super(message);
    }
}
