public class SeatUnavailableException extends BookingException {
    public SeatUnavailableException(String seatNumber) {
        super("Seat " + seatNumber + " is already taken.");
    }
}
