import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingService {
    private BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    public Map<String, String> getSeatMap(int screeningId) {
        // 1. Get all seats from Screening Service (Mock)
        List<String> allSeats = MockServices.MockScreeningService.getLayout(screeningId);
        
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

    public String attemptBooking(int userId, int screeningId, String seatNumber) {
        // 1. Validate Screening
        if (!MockServices.MockScreeningService.isValidScreening(screeningId)) {
            return "Error: Invalid Screening ID";
        }
        
        // 2. Get Price
        BigDecimal price = MockServices.MockScreeningService.getScreeningPrice(screeningId);
        
        // 3. Call DAO to create booking (handles concurrency via SP)
        return bookingDAO.createBooking(userId, screeningId, seatNumber, price);
    }

    public List<Booking> getMyBookings(int userId) {
        return bookingDAO.getUserBookings(userId);
    }

    public String cancelMyBooking(int userId, int bookingId) {
        // In a real app, verify ownership first
        // For now, we trust the caller or assume DAO handles it if we passed userId to DAO
        // Let's add a check here if we had a getBookingById
        
        boolean success = bookingDAO.cancelBooking(bookingId);
        return success ? "Booking Cancelled" : "Cancellation Failed";
    }
}
