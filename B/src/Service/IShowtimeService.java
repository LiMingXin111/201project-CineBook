package Service;

import Model.Showtime;
import Exception.ShowtimeConflictException;
import java.util.List;

public interface IShowtimeService {
    void addShowtime(Showtime showtime) throws ShowtimeConflictException;
    List<Showtime> getAllShowtimes();
    List<Showtime> getShowtimesByTheater(int theaterId);
}


