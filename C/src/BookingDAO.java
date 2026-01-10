import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    // Placeholder connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/movie_db";
    private static final String USER = "root";
    private static final String PASS = "password";

    private Connection getConnection() throws SQLException {
        // In a real app, use a connection pool
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public String createBooking(int userId, int screeningId, String seatNumber, BigDecimal price) {
        String sql = "{CALL create_booking(?, ?, ?, ?, ?)}";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, screeningId);
            stmt.setString(3, seatNumber);
            stmt.setBigDecimal(4, price);
            stmt.registerOutParameter(5, Types.VARCHAR);
            
            stmt.execute();
            
            return stmt.getString(5);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    public List<String> getBookedSeats(int screeningId) {
        List<String> bookedSeats = new ArrayList<>();
        String sql = "SELECT seat_number FROM BOOKINGS WHERE screening_id = ? AND status = 'CONFIRMED'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, screeningId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookedSeats.add(rs.getString("seat_number"));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookedSeats;
    }

    public List<Booking> getUserBookings(int userId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKINGS WHERE user_id = ? ORDER BY booking_time DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = new Booking();
                    b.setBookingId(rs.getInt("booking_id"));
                    b.setUserId(rs.getInt("user_id"));
                    b.setScreeningId(rs.getInt("screening_id"));
                    b.setSeatNumber(rs.getString("seat_number"));
                    b.setStatus(rs.getString("status"));
                    b.setPrice(rs.getBigDecimal("price"));
                    b.setBookingTime(rs.getTimestamp("booking_time"));
                    bookings.add(b);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    public boolean cancelBooking(int bookingId) {
        String sql = "UPDATE BOOKINGS SET status = 'CANCELLED' WHERE booking_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookingId);
            int rows = stmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper for testing without DB
    public void setMockConnection() {
        // TODO: Implement mock connection if needed for unit tests without DB
    }
}
