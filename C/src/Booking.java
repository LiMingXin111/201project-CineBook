import java.math.BigDecimal;
import java.sql.Timestamp;

public class Booking {
    private int bookingId;
    private int userId;
    private int screeningId;
    private String seatNumber;
    private String status;
    private Timestamp bookingTime;
    private BigDecimal price;

    public Booking() {}

    public Booking(int userId, int screeningId, String seatNumber, BigDecimal price) {
        this.userId = userId;
        this.screeningId = screeningId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = "PENDING";
    }

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getScreeningId() { return screeningId; }
    public void setScreeningId(int screeningId) { this.screeningId = screeningId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getBookingTime() { return bookingTime; }
    public void setBookingTime(Timestamp bookingTime) { this.bookingTime = bookingTime; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + bookingId +
                ", user=" + userId +
                ", screening=" + screeningId +
                ", seat='" + seatNumber + '\'' +
                ", status='" + status + '\'' +
                ", price=" + price +
                '}';
    }
}
