import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BookingDAO {
    private String dbUrl;
    private String dbUser;
    private String dbPass;

    public BookingDAO() {
        loadProperties();
    }

    private void loadProperties() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Sorry, unable to find db.properties");
                return;
            }
            prop.load(input);
            this.dbUrl = prop.getProperty("db.url");
            this.dbUser = prop.getProperty("db.user");
            this.dbPass = prop.getProperty("db.password");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPass);
    }

    public void createBooking(int userId, int screeningId, String seatNumber, BigDecimal price) throws BookingException {
        String sql = "{CALL create_booking(?, ?, ?, ?, ?)}";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, screeningId);
            stmt.setString(3, seatNumber);
            stmt.setBigDecimal(4, price);
            stmt.registerOutParameter(5, Types.VARCHAR);
            
            stmt.execute();
            
            String result = stmt.getString(5);
            if (result.startsWith("FAIL")) {
                if (result.contains("already taken")) {
                    throw new SeatUnavailableException(seatNumber);
                } else {
                    throw new BookingException(result);
                }
            }
            
        } catch (SQLException e) {
            throw new BookingException("Database error: " + e.getMessage());
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
            e.printStackTrace(); // In real app, log this
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
}
