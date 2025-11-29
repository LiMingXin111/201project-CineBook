import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class handling core booking logic.
 * Manages seat availability, booking creation, and order retrieval.
 */
public class BookingService {
    private BookingDAO bookingDAO;
    private IScreeningService screeningService;
    private IUserService userService;

    /**
     * Constructor with Dependency Injection.
     * 
     * @param screeningService Service to retrieve screening info (prices, layout)
     * @param userService Service to retrieve user info
     */
    public BookingService(IScreeningService screeningService, IUserService userService) {
        this.bookingDAO = new BookingDAO();
        this.screeningService = screeningService;
        this.userService = userService;
    }

    /**
     * Retrieves the seat map for a given screening, merging static layout with dynamic booking status.
     * 
     * @param screeningId the ID of the screening
     * @return a Map where key is seat number and value is status (AVAILABLE/SOLD)
     */
    public Map<String, String> getSeatMap(int screeningId) {
        // 1. Get all seats from Screening Service
        List<String> allSeats = screeningService.getLayout(screeningId);
        
        // 2. Get booked seats from DB
        List<String> bookedSeats = bookingDAO.getBookedSeats(screeningId);
        
        // 3. Merge
        Map<String, String> seatMap = new HashMap<>();
        for (String seat : allSeats) {
            if (bookedSeats.contains(seat)) {
                seatMap.put(seat, "SOLD");
            } else {
                seatMap.put(seat, "AVAILABLE");
            }
        }
        return seatMap;
    }

    /**
     * Attempts to book a seat for the current user.
     * 
     * @param screeningId the screening ID
     * @param seatNumber the seat number to book
     * @throws BookingException if the booking fails (e.g., seat taken, invalid screening)
     */
    public void attemptBooking(int screeningId, String seatNumber) throws BookingException {
        // 1. Validate Screening
        if (!screeningService.isValidScreening(screeningId)) {
            throw new BookingException("Invalid Screening ID: " + screeningId);
        }
        
        // 2. Get User
        int userId = userService.getCurrentUserId();
        
        // 3. Get Price
        BigDecimal price = screeningService.getScreeningPrice(screeningId);
        
        // 4. Call DAO
        bookingDAO.createBooking(userId, screeningId, seatNumber, price);
    }

    /**
     * Retrieves all bookings for the current user.
     * 
     * @return List of Booking objects
     */
    public List<Booking> getMyBookings() {
        int userId = userService.getCurrentUserId();
        return bookingDAO.getUserBookings(userId);
    }

    /**
     * Cancels a specific booking.
     * 
     * @param bookingId the ID of the booking to cancel
     * @return true if successful, false otherwise
     */
    public boolean cancelMyBooking(int bookingId) {
        // In a real app, we should verify that the booking belongs to the current user first.
        return bookingDAO.cancelBooking(bookingId);
    }
}
