package com.cinebook.model;

public class Showtime {
    private int id;
    private String date;
    private String time;
    private String hall;
    private double price;
    private boolean[] seats;
    
    // Constructors, getters, and setters.
    public Showtime() {}
    
    public Showtime(int id, String date, String time, String hall, double price, boolean[] seats) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.hall = hall;
        this.price = price;
        this.seats = seats;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getHall() { return hall; }
    public void setHall(String hall) { this.hall = hall; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public boolean[] getSeats() { return seats; }
    public void setSeats(boolean[] seats) { this.seats = seats; }
}
