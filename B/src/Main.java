import Dao.MovieDAO;
import Dao.ShowtimeDAO;
import Dao.TheaterDAO;
import Model.Movie;
import Model.Showtime;
import Model.Theater;
import Service.ShowtimeService;
import Exception.ShowtimeConflictException;

import java.time.LocalDateTime;

public class Main {
    void main(String[] args) {

        // Initialize DAOs
        MovieDAO movieDAO = new MovieDAO();
        TheaterDAO theaterDAO = new TheaterDAO();
        ShowtimeDAO showtimeDAO = new ShowtimeDAO();

        // Initialize Service (with conflict checking)
        ShowtimeService showtimeService = new ShowtimeService(showtimeDAO);

        // -------------------------------------
        // Add Movie examples (full 5 parameters)
        // -------------------------------------
        Movie m1 = new Movie(
                1,
                "Avengers: Endgame",
                "Superhero team fights Thanos.",
                "https://example.com/avengers.jpg",
                180
        );

        Movie m2 = new Movie(
                2,
                "Spiderman: No Way Home",
                "Multiverse adventure with 3 Spidermen.",
                "https://example.com/spiderman.jpg",
                150
        );

        movieDAO.add(m1);
        movieDAO.add(m2);

        // -------------------------------------
        // Add Theater examples
        // -------------------------------------
        Theater t1 = new Theater(1, "Hall A", 120);
        Theater t2 = new Theater(2, "Hall B", 100);

        theaterDAO.add(t1);
        theaterDAO.add(t2);

        // -------------------------------------
        // Create Showtime examples (with complete time)
        // -------------------------------------
        Showtime s1 = new Showtime(
                1,
                1, // movieId
                1, // theaterId
                LocalDateTime.of(2025, 1, 1, 13, 0),
                LocalDateTime.of(2025, 1, 1, 15, 30)
        );

        Showtime s2 = new Showtime(
                2,
                2,
                2,
                LocalDateTime.of(2025, 1, 1, 16, 0),
                LocalDateTime.of(2025, 1, 1, 18, 30)
        );

        try {
            showtimeService.addShowtime(s1);
            showtimeService.addShowtime(s2);
        } catch (ShowtimeConflictException e) {
            System.out.println("Showtime error: " + e.getMessage());
        }

        // -------------------------------------
        // Simple display to check if successful
        // -------------------------------------
        System.out.println("--- Movies Loaded ---");
        for (Movie m : movieDAO.getAll()) {
            System.out.println(m.getId() + " | " + m.getTitle());
        }

        System.out.println("--- Theaters Loaded ---");
        for (Theater t : theaterDAO.getAll()) {
            System.out.println(t.getId() + " | " + t.getName());
        }

        System.out.println("--- Showtimes Loaded ---");
        for (Showtime s : showtimeDAO.getAll()) {
            System.out.println("Showtime " + s.getId() +
                    " | Movie " + s.getMovieId() +
                    " | Theater " + s.getTheaterId());
        }
    }
}
