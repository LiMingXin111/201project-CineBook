package com.cinebook.model;

import java.util.List;

public class Order {
    private String id;
    private int userId;
    private int movieId;
    private String movieTitle;
    private int showtimeId;
    private String showtime;
    private String hall;
    private String seats;
    private List<Integer> seatIndices;
    private double price;
    private double total;
    private String orderDate;
    private String status;

    public Order() {}

    public Order(String id, int userId, int movieId, String movieTitle, int showtimeId, String showtime,
                 String hall, String seats, List<Integer> seatIndices, double price, double total,
                 String orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.showtimeId = showtimeId;
        this.showtime = showtime;
        this.hall = hall;
        this.seats = seats;
        this.seatIndices = seatIndices;
        this.price = price;
        this.total = total;
        this.orderDate = orderDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getShowtime() {
        return showtime;
    }

    public void setShowtime(String showtime) {
        this.showtime = showtime;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public List<Integer> getSeatIndices() {
        return seatIndices;
    }

    public void setSeatIndices(List<Integer> seatIndices) {
        this.seatIndices = seatIndices;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
