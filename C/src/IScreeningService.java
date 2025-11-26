import java.math.BigDecimal;
import java.util.List;

public interface IScreeningService {
    /**
     * Checks if a screening ID is valid.
     * @param screeningId the screening ID to check
     * @return true if valid, false otherwise
     */
    boolean isValidScreening(int screeningId);

    /**
     * Gets the price for a specific screening.
     * @param screeningId the screening ID
     * @return the price
     */
    BigDecimal getScreeningPrice(int screeningId);

    /**
     * Gets the seat layout for a specific screening.
     * @param screeningId the screening ID
     * @return a list of seat numbers (e.g., "A1", "A2")
     */
    List<String> getLayout(int screeningId);
}
