import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    // Inject dependencies manually for this CLI demo
    private static IScreeningService screeningService = new MockServices.MockScreeningService();
    private static IUserService userService = new MockServices.MockUserService();
    private static BookingService bookingService = new BookingService(screeningService, userService);
    
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleWebServer webServer;

    public static void main(String[] args) {
        System.out.println("=== Movie Booking System (Module C) ===");
        
        // Start web server
        startWebServer();
        
        // Keep the CLI interface as well
        while (true) {
            System.out.println("\n1. View Seats (Screening 1)");
            System.out.println("2. Book a Seat");
            System.out.println("3. My Orders");
            System.out.println("4. Cancel Order");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    viewSeats();
                    break;
                case "2":
                    bookSeat();
                    break;
                case "3":
                    myOrders();
                    break;
                case "4":
                    cancelOrder();
                    break;
                case "5":
                    System.out.println("Goodbye!");
                    if (webServer != null) {
                        webServer.stop();
                    }
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private static void startWebServer() {
        try {
            webServer = new SimpleWebServer(bookingService);
            webServer.start(8080);
        } catch (Exception e) {
            System.err.println("Failed to start web server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void viewSeats() {
        int screeningId = 1; // Mock screening
        System.out.println("\n--- Seat Map (Screening " + screeningId + ") ---");
        Map<String, String> seatMap = bookingService.getSeatMap(screeningId);
        
        String[] rows = {"A", "B", "C"};
        for (String row : rows) {
            for (int i = 1; i <= 3; i++) {
                String seat = row + i;
                String status = seatMap.getOrDefault(seat, "UNKNOWN");
                String symbol = "AVAILABLE".equals(status) ? "[ ]" : "[X]";
                System.out.print(seat + symbol + "  ");
            }
            System.out.println();
        }
    }

    private static void bookSeat() {
        System.out.print("Enter Screening ID (default 1): ");
        String sIdStr = scanner.nextLine();
        int sId = sIdStr.isEmpty() ? 1 : Integer.parseInt(sIdStr);
        
        System.out.print("Enter Seat Number (e.g. A1): ");
        String seat = scanner.nextLine();
        
        System.out.println("Attempting to book " + seat + "...");
        try {
            bookingService.attemptBooking(sId, seat);
            System.out.println("SUCCESS: Booking confirmed.");
        } catch (SeatUnavailableException e) {
            System.out.println("FAIL: " + e.getMessage());
        } catch (BookingException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void myOrders() {
        System.out.println("\n--- My Orders ---");
        List<Booking> bookings = bookingService.getMyBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking b : bookings) {
                System.out.println(b);
            }
        }
    }

    private static void cancelOrder() {
        System.out.print("Enter Booking ID to cancel: ");
        try {
            int bookingId = Integer.parseInt(scanner.nextLine());
            boolean success = bookingService.cancelMyBooking(bookingId);
            System.out.println(success ? "Booking Cancelled" : "Cancellation Failed");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }
}