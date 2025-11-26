/**
 * Represents a movie entity with its basic information.
 * Contains details like title, description, poster URL and duration.
 */
package Model;

public class Movie {
    private int id;               //Unique identifier for the movie
    private String title;         //Title of the movie
    private String description;   //Description/summary of the movie
    private String posterUrl;     //URL to the movie poster image
    private int duration;         //Duration of the movie in minutes

    /**
     * Default constructor for Movie class
     */
    public Movie(){}

    /**
     * Parameterized constructor for Movie class
     * @param id Unique identifier for the movie
     * @param title Title of the movie
     * @param description Description/summary of the movie
     * @param posterUrl URL to the movie poster image
     * @param duration Duration of the movie in minutes
     */
    public Movie(int id, String title, String description, String posterUrl, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.duration = duration;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public String getPosterUrl() {return posterUrl;}
    public void setPosterUrl(String posterUrl) {this.posterUrl = posterUrl;}

    public int getDuration() {return duration;}
    public void setDuration(int duration) {this.duration = duration;}
}
