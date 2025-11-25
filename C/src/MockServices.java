import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MockServices {

    public static class MockUserService {
        public static int getCurrentUserId() {
            // Simulate a logged-in user
            return 101;
        }
    }

    public static class MockScreeningService {
        public static boolean isValidScreening(int screeningId) {
            // Simulate checking if screening exists
            return screeningId == 1;
        }

        public static BigDecimal getScreeningPrice(int screeningId) {
            // Simulate fetching price
            return new BigDecimal("12.50");
        }
        
        public static List<String> getLayout(int screeningId) {
            // Simulate a 3x3 layout
            List<String> seats = new ArrayList<>();
            seats.add("A1"); seats.add("A2"); seats.add("A3");
            seats.add("B1"); seats.add("B2"); seats.add("B3");
            seats.add("C1"); seats.add("C2"); seats.add("C3");
            return seats;
        }
    }
}
