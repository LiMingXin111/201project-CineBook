package com.cinebook.model;

import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String poster;
    private String description;
    private List<Showtime> showtimes;
    
    // Constructors, getters, and setters.
    public Movie() {}
    
    public Movie(int id, String title, String poster, String description, List<Showtime> showtimes) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.description = description;
        this.showtimes = showtimes;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<Showtime> getShowtimes() { return showtimes; }
    public void setShowtimes(List<Showtime> showtimes) { this.showtimes = showtimes; }
}
